package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitAnswerRequest;
import com.zntk.dto.SubmitExamRequest;
import com.zntk.entity.AnswerRecord;
import com.zntk.entity.ExamRecord;
import com.zntk.entity.Paper;
import com.zntk.entity.PaperQuestion;
import com.zntk.entity.Question;
import com.zntk.mapper.AnswerRecordMapper;
import com.zntk.mapper.ExamRecordMapper;
import com.zntk.mapper.PaperMapper;
import com.zntk.mapper.PaperQuestionMapper;
import com.zntk.mapper.QuestionMapper;
import com.zntk.service.ExamService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 考试业务实现类。
 *
 * 负责开始考试、提交答案、自动判分、查询考试结果。
 */
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRecordMapper examRecordMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public ExamServiceImpl(
            ExamRecordMapper examRecordMapper,
            AnswerRecordMapper answerRecordMapper,
            PaperMapper paperMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper,
            StringRedisTemplate stringRedisTemplate
    ) {
        this.examRecordMapper = examRecordMapper;
        this.answerRecordMapper = answerRecordMapper;
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 开始考试。
     */
    @Override
    public Long startExam(StartExamRequest request) {
        // 1. 查询试卷是否存在
        Paper paper = paperMapper.selectById(request.getPaperId());

        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 2. 创建考试记录
        ExamRecord examRecord = new ExamRecord();
        examRecord.setPaperId(request.getPaperId());
        examRecord.setUserId(request.getUserId());
        examRecord.setTotalScore(paper.getTotalScore());
        examRecord.setUserScore(0);
        examRecord.setStatus(0);
        examRecord.setStartTime(LocalDateTime.now());

        examRecordMapper.insert(examRecord);

        return examRecord.getId();
    }

    /**
     * 提交考试并自动判分。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitExam(SubmitExamRequest request) {
        // 根据考试记录 ID 生成 Redis 锁 key
        String lockKey = "exam:submit:" + request.getExamRecordId();

        // 尝试加锁。
        //
        // setIfAbsent 等价于 Redis 的 SETNX：
        // 如果 key 不存在，就设置成功，返回 true；
        // 如果 key 已经存在，说明有其他请求正在提交，返回 false。
        //
        // 这里设置 30 秒过期时间，避免服务异常时锁一直不释放。
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(locked)) {
            throw new RuntimeException("考试正在提交中，请勿重复操作");
        }

        try {
            // 下面开始执行原来的提交考试逻辑

            // 1. 查询考试记录
            ExamRecord examRecord = examRecordMapper.selectById(request.getExamRecordId());

            if (examRecord == null) {
                throw new RuntimeException("考试记录不存在");
            }

            if (examRecord.getStatus() != null && examRecord.getStatus() == 1) {
                throw new RuntimeException("考试已提交，不能重复提交");
            }

            // 2. 查询试卷题目关联，后面要根据题目分值判分
            LambdaQueryWrapper<PaperQuestion> paperQuestionWrapper = new LambdaQueryWrapper<>();
            paperQuestionWrapper.eq(PaperQuestion::getPaperId, examRecord.getPaperId());

            List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(paperQuestionWrapper);

            int userScore = 0;

            // 3. 遍历用户提交的答案
            for (SubmitAnswerRequest answerRequest : request.getAnswers()) {
                // 3.1 查询题目
                Question question = questionMapper.selectById(answerRequest.getQuestionId());

                if (question == null) {
                    throw new RuntimeException("题目不存在：" + answerRequest.getQuestionId());
                }

                // 3.2 找到这道题在试卷中的分值
                Integer questionScore = findQuestionScore(paperQuestions, answerRequest.getQuestionId());

                // 3.3 判断答案是否正确
                boolean correct = question.getAnswer() != null
                        && question.getAnswer().equalsIgnoreCase(answerRequest.getUserAnswer());

                int score = correct ? questionScore : 0;

                // 3.4 保存答题记录
                AnswerRecord answerRecord = new AnswerRecord();
                answerRecord.setExamRecordId(examRecord.getId());
                answerRecord.setQuestionId(answerRequest.getQuestionId());
                answerRecord.setUserAnswer(answerRequest.getUserAnswer());
                answerRecord.setCorrectAnswer(question.getAnswer());
                answerRecord.setIsCorrect(correct ? 1 : 0);
                answerRecord.setScore(score);

                answerRecordMapper.insert(answerRecord);

                // 3.5 累加用户得分
                userScore += score;
            }

            // 4. 更新考试记录
            examRecord.setUserScore(userScore);
            examRecord.setStatus(1);
            examRecord.setSubmitTime(LocalDateTime.now());

            examRecordMapper.updateById(examRecord);

            return true;
        } finally {
            // 不管提交成功还是失败，都释放锁
            stringRedisTemplate.delete(lockKey);
        }
    }

    /**
     * 从试卷题目列表中找到某道题的分值。
     */
    private Integer findQuestionScore(List<PaperQuestion> paperQuestions, Long questionId) {
        for (PaperQuestion paperQuestion : paperQuestions) {
            if (paperQuestion.getQuestionId().equals(questionId)) {
                return paperQuestion.getScore();
            }
        }

        throw new RuntimeException("题目不属于当前试卷：" + questionId);
    }

    /**
     * 查询考试结果。
     */
    @Override
    public ExamResultResponse getExamResult(Long id) {
        // 1. 查询考试记录
        ExamRecord examRecord = examRecordMapper.selectById(id);

        if (examRecord == null) {
            throw new RuntimeException("考试记录不存在");
        }

        // 2. 查询答题记录
        LambdaQueryWrapper<AnswerRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AnswerRecord::getExamRecordId, id);

        List<AnswerRecord> answerRecords = answerRecordMapper.selectList(wrapper);

        // 3. 组装返回结果
        ExamResultResponse response = new ExamResultResponse();
        response.setExamRecord(examRecord);
        response.setAnswerRecords(answerRecords);

        return response;
    }
}