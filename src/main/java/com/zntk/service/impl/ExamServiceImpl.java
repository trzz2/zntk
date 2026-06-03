package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.common.ForbiddenException;
import com.zntk.common.UserContext;
import com.zntk.dto.ExamHistoryResponse;
import com.zntk.dto.ExamRankingResponse;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitAnswerRequest;
import com.zntk.dto.SubmitExamRequest;
import com.zntk.entity.AnswerRecord;
import com.zntk.entity.ExamRecord;
import com.zntk.entity.Paper;
import com.zntk.entity.PaperQuestion;
import com.zntk.entity.Question;
import com.zntk.entity.WrongQuestion;
import com.zntk.mapper.AnswerRecordMapper;
import com.zntk.mapper.ExamRecordMapper;
import com.zntk.mapper.PaperMapper;
import com.zntk.mapper.PaperQuestionMapper;
import com.zntk.mapper.QuestionMapper;
import com.zntk.mapper.WrongQuestionMapper;
import com.zntk.service.ExamService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private static final String EXAM_RANKING_KEY_PREFIX = "zntk:exam:ranking:";

    private final ExamRecordMapper examRecordMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final WrongQuestionMapper wrongQuestionMapper;

    public ExamServiceImpl(
            ExamRecordMapper examRecordMapper,
            AnswerRecordMapper answerRecordMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper,
            StringRedisTemplate stringRedisTemplate,
            WrongQuestionMapper wrongQuestionMapper,
            PaperMapper paperMapper
    ) {
        this.examRecordMapper = examRecordMapper;
        this.answerRecordMapper = answerRecordMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.wrongQuestionMapper = wrongQuestionMapper;
        this.paperMapper = paperMapper;
    }

    @Override
    public Long startExam(StartExamRequest request) {
        Paper paper = paperMapper.selectById(request.getPaperId());
        if (paper == null) {
            throw new RuntimeException("Paper not found");
        }

        ExamRecord examRecord = new ExamRecord();
        examRecord.setPaperId(request.getPaperId());
        examRecord.setUserId(request.getUserId());
        examRecord.setTotalScore(paper.getTotalScore());
        examRecord.setUserScore(0);
        examRecord.setStatus(0);
        examRecord.setStartTime(LocalDateTime.now());
        examRecord.setCreateTime(LocalDateTime.now());
        examRecord.setUpdateTime(LocalDateTime.now());
        examRecord.setDeleted(0);
        examRecordMapper.insert(examRecord);

        return examRecord.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitExam(SubmitExamRequest request) {
        String lockKey = "exam:submit:" + request.getExamRecordId();
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("Exam is submitting, please do not repeat");
        }

        try {
            ExamRecord examRecord = examRecordMapper.selectById(request.getExamRecordId());
            if (examRecord == null) {
                throw new RuntimeException("Exam record not found");
            }

            assertCurrentUserOwnsExam(examRecord);

            if (Integer.valueOf(1).equals(examRecord.getStatus())) {
                throw new RuntimeException("Exam already submitted");
            }

            Paper paper = paperMapper.selectById(examRecord.getPaperId());
            if (paper == null) {
                throw new RuntimeException("Paper not found");
            }

            if (paper.getDurationMinutes() != null
                    && examRecord.getStartTime() != null
                    && LocalDateTime.now().isAfter(examRecord.getStartTime().plusMinutes(paper.getDurationMinutes()))) {
                throw new RuntimeException("Exam timeout");
            }

            LambdaQueryWrapper<PaperQuestion> paperQuestionWrapper = new LambdaQueryWrapper<>();
            paperQuestionWrapper.eq(PaperQuestion::getPaperId, examRecord.getPaperId());
            List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(paperQuestionWrapper);

            int userScore = 0;
            for (SubmitAnswerRequest answerRequest : request.getAnswers()) {
                Question question = questionMapper.selectById(answerRequest.getQuestionId());
                if (question == null) {
                    throw new RuntimeException("Question not found: " + answerRequest.getQuestionId());
                }

                Integer questionScore = findQuestionScore(paperQuestions, answerRequest.getQuestionId());
                boolean correct = isAnswerCorrect(question, answerRequest.getUserAnswer());
                int score = correct ? questionScore : 0;

                AnswerRecord answerRecord = new AnswerRecord();
                answerRecord.setExamRecordId(examRecord.getId());
                answerRecord.setQuestionId(answerRequest.getQuestionId());
                answerRecord.setUserAnswer(answerRequest.getUserAnswer());
                answerRecord.setCorrectAnswer(question.getAnswer());
                answerRecord.setIsCorrect(correct ? 1 : 0);
                answerRecord.setScore(score);
                answerRecord.setCreateTime(LocalDateTime.now());
                answerRecord.setUpdateTime(LocalDateTime.now());
                answerRecord.setDeleted(0);
                answerRecordMapper.insert(answerRecord);

                if (!correct) {
                    saveOrUpdateWrongQuestion(examRecord, answerRequest, question);
                }

                userScore += score;
            }

            examRecord.setUserScore(userScore);
            examRecord.setStatus(1);
            examRecord.setSubmitTime(LocalDateTime.now());
            examRecord.setUpdateTime(LocalDateTime.now());
            examRecordMapper.updateById(examRecord);

            stringRedisTemplate.opsForZSet().add(
                    EXAM_RANKING_KEY_PREFIX + examRecord.getPaperId(),
                    String.valueOf(examRecord.getUserId()),
                    (double) userScore
            );

            return true;
        } finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public ExamResultResponse getExamResult(Long id) {
        ExamRecord examRecord = examRecordMapper.selectById(id);
        if (examRecord == null) {
            throw new RuntimeException("Exam record not found");
        }

        assertCurrentUserOwnsExam(examRecord);

        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getExamRecordId, id);

        ExamResultResponse response = new ExamResultResponse();
        response.setExamRecord(examRecord);
        response.setAnswerRecords(answerRecordMapper.selectList(wrapper));
        return response;
    }

    @Override
    public List<ExamRankingResponse> getRanking(Long paperId, Integer limit) {
        int queryLimit = limit == null || limit <= 0 ? 10 : limit;

        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(EXAM_RANKING_KEY_PREFIX + paperId, 0, queryLimit - 1);

        List<ExamRankingResponse> rankingList = new ArrayList<>();
        if (tuples == null || tuples.isEmpty()) {
            return rankingList;
        }

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            ExamRankingResponse response = new ExamRankingResponse();
            response.setRank(rank++);
            response.setUserId(Long.valueOf(tuple.getValue()));
            response.setScore(tuple.getScore());
            rankingList.add(response);
        }
        return rankingList;
    }

    @Override
    public List<ExamHistoryResponse> listHistory(Long userId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamRecord::getUserId, userId);
        wrapper.orderByDesc(ExamRecord::getStartTime);

        List<ExamHistoryResponse> historyList = new ArrayList<>();
        for (ExamRecord examRecord : examRecordMapper.selectList(wrapper)) {
            ExamHistoryResponse response = new ExamHistoryResponse();
            response.setExamRecordId(examRecord.getId());
            response.setPaperId(examRecord.getPaperId());
            response.setTotalScore(examRecord.getTotalScore());
            response.setUserScore(examRecord.getUserScore());
            response.setStatus(examRecord.getStatus());
            response.setStartTime(examRecord.getStartTime());
            response.setSubmitTime(examRecord.getSubmitTime());

            Paper paper = paperMapper.selectById(examRecord.getPaperId());
            if (paper != null) {
                response.setPaperTitle(paper.getTitle());
            }
            historyList.add(response);
        }
        return historyList;
    }

    private Integer findQuestionScore(List<PaperQuestion> paperQuestions, Long questionId) {
        for (PaperQuestion paperQuestion : paperQuestions) {
            if (paperQuestion.getQuestionId().equals(questionId)) {
                return paperQuestion.getScore();
            }
        }
        throw new RuntimeException("Question does not belong to current paper: " + questionId);
    }

    private boolean isAnswerCorrect(Question question, String userAnswer) {
        if (question.getAnswer() == null || userAnswer == null) {
            return false;
        }

        if (Integer.valueOf(2).equals(question.getQuestionType())) {
            return normalizeMultiAnswer(question.getAnswer()).equals(normalizeMultiAnswer(userAnswer));
        }

        return question.getAnswer().equalsIgnoreCase(userAnswer.trim());
    }

    private String normalizeMultiAnswer(String answer) {
        return Arrays.stream(answer.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(String::toUpperCase)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(","));
    }

    private void saveOrUpdateWrongQuestion(
            ExamRecord examRecord,
            SubmitAnswerRequest answerRequest,
            Question question
    ) {
        LambdaQueryWrapper<WrongQuestion> wrongWrapper = new LambdaQueryWrapper<>();
        wrongWrapper.eq(WrongQuestion::getUserId, examRecord.getUserId());
        wrongWrapper.eq(WrongQuestion::getQuestionId, answerRequest.getQuestionId());

        WrongQuestion oldWrongQuestion = wrongQuestionMapper.selectOne(wrongWrapper);
        LocalDateTime now = LocalDateTime.now();

        if (oldWrongQuestion == null) {
            WrongQuestion wrongQuestion = new WrongQuestion();
            wrongQuestion.setUserId(examRecord.getUserId());
            wrongQuestion.setQuestionId(answerRequest.getQuestionId());
            wrongQuestion.setPaperId(examRecord.getPaperId());
            wrongQuestion.setExamRecordId(examRecord.getId());
            wrongQuestion.setWrongAnswer(answerRequest.getUserAnswer());
            wrongQuestion.setCorrectAnswer(question.getAnswer());
            wrongQuestion.setWrongCount(1);
            wrongQuestion.setLastWrongTime(now);
            wrongQuestion.setCreateTime(now);
            wrongQuestion.setUpdateTime(now);
            wrongQuestion.setDeleted(0);
            wrongQuestionMapper.insert(wrongQuestion);
        } else {
            oldWrongQuestion.setWrongCount(oldWrongQuestion.getWrongCount() + 1);
            oldWrongQuestion.setWrongAnswer(answerRequest.getUserAnswer());
            oldWrongQuestion.setCorrectAnswer(question.getAnswer());
            oldWrongQuestion.setPaperId(examRecord.getPaperId());
            oldWrongQuestion.setExamRecordId(examRecord.getId());
            oldWrongQuestion.setLastWrongTime(now);
            oldWrongQuestion.setUpdateTime(now);
            wrongQuestionMapper.updateById(oldWrongQuestion);
        }
    }

    private void assertCurrentUserOwnsExam(ExamRecord examRecord) {
        if (UserContext.isAdmin()) {
            return;
        }
        if (!UserContext.getUserId().equals(examRecord.getUserId())) {
            throw new ForbiddenException("Cannot access other user's exam record");
        }
    }
}
