package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.List;
import java.util.Set;
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
    private final WrongQuestionMapper wrongQuestionMapper;
    /**
     * 考试排行榜 Redis key 前缀
     *
     * 最终完整 key 长这样：
     * zntk:exam:ranking:3001
     *
     * 其中 3001 是 paperId，表示某张试卷的排行榜。
     */
    private static final String EXAM_RANKING_KEY_PREFIX = "zntk:exam:ranking:";

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
                // 如果这道题答错了，就保存到错题本。
// correct 是前面判题得到的布尔值：true 表示答对，false 表示答错。
                // 如果这道题答错了，就保存到错题本。
// 这里做了一个优化：
// 同一个用户同一道题如果已经存在错题记录，就不重复插入，改为更新 wrongCount。
                if (!correct) {
                    // 先根据 userId + questionId 查询这道题之前是否已经进过错题本。
                    LambdaQueryWrapper<WrongQuestion> wrongWrapper = new LambdaQueryWrapper<>();
                    wrongWrapper.eq(WrongQuestion::getUserId, examRecord.getUserId());
                    wrongWrapper.eq(WrongQuestion::getQuestionId, answerRequest.getQuestionId());

                    WrongQuestion oldWrongQuestion = wrongQuestionMapper.selectOne(wrongWrapper);

                    // 当前时间，下面新增或更新都要用。
                    LocalDateTime now = LocalDateTime.now();

                    if (oldWrongQuestion == null) {
                        // 情况 1：以前没错过这道题。
                        // 新增一条错题记录。
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
                        // 情况 2：以前已经错过这道题。
                        // 不再新增记录，而是更新原来的错题记录。
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


                // 3.5 累加用户得分
                userScore += score;
            }

            // 4. 更新考试记录
            examRecord.setUserScore(userScore);
            examRecord.setStatus(1);
            examRecord.setSubmitTime(LocalDateTime.now());

            examRecordMapper.updateById(examRecord);

            // 把本次考试成绩写入 Redis 排行榜。
// key 按试卷区分：zntk:exam:ranking:{paperId}
// member 保存 userId，表示是哪位用户。
// score 保存 userScore，表示这个用户本次考试分数。
            String rankingKey = EXAM_RANKING_KEY_PREFIX + examRecord.getPaperId();

            stringRedisTemplate.opsForZSet().add(
                    rankingKey,
                    String.valueOf(examRecord.getUserId()),
                    (double) userScore
            );

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

    @Override
    public List<ExamRankingResponse> getRanking(Long paperId, Integer limit) {
        // 如果前端没有传 limit，就默认查询前 10 名。
        if (limit == null || limit <= 0) {
            limit = 10;
        }

        // Redis 排行榜 key。
        // 例如 paperId = 3001，则 key = zntk:exam:ranking:3001
        String rankingKey = EXAM_RANKING_KEY_PREFIX + paperId;

        // reverseRangeWithScores 表示：
        // 从 ZSet 中按 score 从高到低查询。
        //
        // 0 表示从第 1 名开始。
        // limit - 1 表示查到第 limit 名。
        //
        // 返回值 Set<TypedTuple<String>> 里：
        // value 是 member，也就是 userId 字符串。
        // score 是分数。
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet()
                        .reverseRangeWithScores(rankingKey, 0, limit - 1);

        // 创建一个列表，用来装返回给前端的排行榜数据。
        List<ExamRankingResponse> rankingList = new ArrayList<>();

        // 如果 Redis 里还没有排行榜数据，直接返回空列表。
        if (tuples == null || tuples.isEmpty()) {
            return rankingList;
        }

        // rank 表示当前排名，从 1 开始。
        int rank = 1;

        // 遍历 Redis 查出来的排行榜数据。
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            ExamRankingResponse response = new ExamRankingResponse();

            // tuple.getValue() 是 Redis ZSet 的 member。
            // 我们前面存进去的是 userId 字符串，所以这里转成 Long。
            response.setUserId(Long.valueOf(tuple.getValue()));

            // tuple.getScore() 是 Redis ZSet 的 score，也就是考试分数。
            response.setScore(tuple.getScore());

            // 设置排名。
            response.setRank(rank);

            rankingList.add(response);

            // 下一个人排名 +1。
            rank++;
        }

        return rankingList;
    }
}