package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.PaperCreateRequest;
import com.zntk.dto.PaperDetailResponse;
import com.zntk.dto.PaperQuestionRequest;
import com.zntk.dto.RandomPaperRequest;
import com.zntk.entity.Paper;
import com.zntk.entity.PaperQuestion;
import com.zntk.entity.Question;
import com.zntk.enums.StatusEnum;
import com.zntk.mapper.PaperMapper;
import com.zntk.mapper.PaperQuestionMapper;
import com.zntk.mapper.QuestionMapper;
import com.zntk.service.PaperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 试卷业务实现类。
 *
 * 负责创建试卷、保存试卷题目关系、查询试卷详情。
 */
@Service
public class PaperServiceImpl implements PaperService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    /**
     * 构造器注入 Mapper。
     */
    public PaperServiceImpl(
            PaperMapper paperMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper
    ) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
    }

    /**
     * 创建试卷。
     *
     * @Transactional 表示开启事务。
     * 如果创建试卷成功，但保存题目关系失败，整个操作会回滚。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(PaperCreateRequest request) {
        // 1. 校验题目列表不能为空
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new RuntimeException("试卷题目不能为空");
        }

        // 2. 创建 Paper 实体，对应 paper 表
        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setDescription(request.getDescription());
        paper.setDurationMinutes(request.getDurationMinutes());

        // 如果前端没有传 status，默认启用
        paper.setStatus(request.getStatus() == null
                ? StatusEnum.ENABLED.getCode()
                : request.getStatus());

        // 先设置总分为 0，后面根据题目分值累加
        paper.setTotalScore(0);

        // 3. 先插入 paper 主表
        paperMapper.insert(paper);

        // 插入后，MyBatis-Plus 会把生成的 ID 回填到 paper.id
        Long paperId = paper.getId();

        // 4. 遍历前端传来的题目配置，保存到 paper_question 表
        int totalScore = 0;

        for (PaperQuestionRequest questionRequest : request.getQuestions()) {
            // 4.1 先确认题目是否存在
            Question question = questionMapper.selectById(questionRequest.getQuestionId());

            if (question == null) {
                throw new RuntimeException("题目不存在：" + questionRequest.getQuestionId());
            }

            // 4.2 创建试卷题目关联实体
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(questionRequest.getQuestionId());
            paperQuestion.setScore(questionRequest.getScore());
            paperQuestion.setSortOrder(questionRequest.getSortOrder());

            // 4.3 插入 paper_question 表
            paperQuestionMapper.insert(paperQuestion);

            // 4.4 累加总分
            totalScore += questionRequest.getScore();
        }

        // 5. 更新试卷总分
        paper.setTotalScore(totalScore);
        paperMapper.updateById(paper);

        return paperId;
    }

    /**
     * 查询试卷详情。
     */
    @Override
    public PaperDetailResponse getPaperById(Long id) {
        // 1. 查询试卷主表
        Paper paper = paperMapper.selectById(id);

        if (paper == null) {
            throw new RuntimeException("试卷不存在");
        }

        // 2. 查询试卷题目关联表
        LambdaQueryWrapper<PaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperQuestion::getPaperId, id);
        wrapper.orderByAsc(PaperQuestion::getSortOrder);

        List<PaperQuestion> paperQuestions = paperQuestionMapper.selectList(wrapper);

        // 3. 根据关联表里的 questionId 查询题目
        List<Question> questions = new ArrayList<>();

        for (PaperQuestion paperQuestion : paperQuestions) {
            Question question = questionMapper.selectById(paperQuestion.getQuestionId());

            if (question != null) {
                questions.add(question);
            }
        }

        // 4. 组装返回结果
        PaperDetailResponse response = new PaperDetailResponse();
        response.setPaper(paper);
        response.setQuestions(questions);

        return response;
    }

    @Override
    public Long randomPaper(RandomPaperRequest request) {
        // 1. 按条件查询题目。
        // LambdaQueryWrapper 是 MyBatis-Plus 的条件构造器，
        // 用来拼接 WHERE 条件。
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();

        // WHERE question_type = ?
        questionWrapper.eq(Question::getQuestionType, request.getQuestionType());

        // WHERE difficulty = ?
        questionWrapper.eq(Question::getDifficulty, request.getDifficulty());

        // WHERE knowledge_point LIKE ?
        // 用 like 是为了支持模糊匹配。
        // 例如传 Redis，可以匹配 knowledge_point 包含 Redis 的题目。
        questionWrapper.like(Question::getKnowledgePoint, request.getKnowledgePoint());

        // 只抽启用状态的题目。
        // status = 1 表示启用。
        questionWrapper.eq(Question::getStatus, 1);

        List<Question> questions = questionMapper.selectList(questionWrapper);

        // 2. 判断题目数量够不够。
        if (questions.size() < request.getQuestionCount()) {
            throw new RuntimeException("符合条件的题目数量不足，无法组卷");
        }

        // 3. 打乱题目列表。
        // Collections.shuffle 会把 List 中的数据随机打乱顺序。
        Collections.shuffle(questions);

        // 4. 截取前 questionCount 道题。
        // subList(0, questionCount) 表示从打乱后的列表中取前 N 道。
        List<Question> selectedQuestions = questions.subList(0, request.getQuestionCount());

        // 5. 创建试卷 paper。
        Paper paper = new Paper();

        paper.setTitle(request.getTitle());
        paper.setDescription(request.getDescription());
        paper.setDurationMinutes(request.getDurationMinutes());

        // 总分 = 题目数量 * 每题分数。
        paper.setTotalScore(request.getQuestionCount() * request.getScorePerQuestion());

        // 1 表示启用。
        paper.setStatus(1);

        paper.setCreateTime(LocalDateTime.now());
        paper.setUpdateTime(LocalDateTime.now());
        paper.setDeleted(0);

        paperMapper.insert(paper);

        // MyBatis-Plus 插入成功后，会把生成的主键 ID 回填到 paper.id。
        Long paperId = paper.getId();

        // 6. 创建试卷和题目的关联记录。
        int sortOrder = 1;

        for (Question question : selectedQuestions) {
            PaperQuestion paperQuestion = new PaperQuestion();

            // 这条关联属于哪张试卷。
            paperQuestion.setPaperId(paperId);

            // 这张试卷包含哪道题。
            paperQuestion.setQuestionId(question.getId());

            // 每道题多少分。
            paperQuestion.setScore(request.getScorePerQuestion());

            // 题目在试卷中的顺序。
            paperQuestion.setSortOrder(sortOrder);

            paperQuestion.setCreateTime(LocalDateTime.now());
            paperQuestion.setDeleted(0);

            paperQuestionMapper.insert(paperQuestion);

            sortOrder++;
        }

        return paperId;
    }
}