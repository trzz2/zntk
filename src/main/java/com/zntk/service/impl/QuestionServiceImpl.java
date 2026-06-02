package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.dto.QuestionOptionRequest;
import com.zntk.entity.Question;
import com.zntk.entity.QuestionOption;
import com.zntk.mapper.QuestionMapper;
import com.zntk.mapper.QuestionOptionMapper;
import com.zntk.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 题目业务实现类。
 *
 * 这里负责题目相关业务逻辑：
 * 查询题目、新增题目、保存选项、修改题目、删除题目等。
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    /**
     * 构造器注入 Mapper。
     *
     * Spring 会自动传入：
     * 1. QuestionMapper：操作 question 表
     * 2. QuestionOptionMapper：操作 question_option 表
     */
    public QuestionServiceImpl(
            QuestionMapper questionMapper,
            QuestionOptionMapper questionOptionMapper
    ) {
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint) {
        // 创建 MyBatis-Plus 查询条件构造器，用来拼接 where 条件
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 如果传了题型，就拼接 question_type = ?
        wrapper.eq(questionType != null, Question::getQuestionType, questionType);

        // 如果传了难度，就拼接 difficulty = ?
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);

        // 如果传了知识点，就拼接 knowledge_point like ?
        wrapper.like(
                knowledgePoint != null && !knowledgePoint.isBlank(),
                Question::getKnowledgePoint,
                knowledgePoint
        );

        // 按创建时间倒序，最新题目在前面
        wrapper.orderByDesc(Question::getCreateTime);

        return questionMapper.selectList(wrapper);
    }

    @Override
    public QuestionDetailResponse getQuestionById(Long id) {
        // 先查询题目主表
        Question question = questionMapper.selectById(id);

        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 再根据题目 ID 查询选项表
        LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
        optionWrapper.eq(QuestionOption::getQuestionId, id);
        optionWrapper.orderByAsc(QuestionOption::getSortOrder);

        List<QuestionOption> options = questionOptionMapper.selectList(optionWrapper);

        // 组装返回对象：题目基础信息 + 选项列表
        QuestionDetailResponse response = new QuestionDetailResponse();
        response.setQuestion(question);
        response.setOptions(options);

        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuestion(QuestionCreateRequest request) {
        // 1. 校验题目基础信息
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("题干不能为空");
        }

        if (request.getQuestionType() == null) {
            throw new RuntimeException("题型不能为空");
        }

        if (request.getDifficulty() == null) {
            throw new RuntimeException("难度不能为空");
        }

        // 2. 把请求 DTO 转成数据库实体 Question
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setKnowledgePoint(request.getKnowledgePoint());
        question.setAnswer(request.getAnswer());
        question.setAnalysis(request.getAnalysis());

        // 如果前端没有传 status，就默认启用
        question.setStatus(request.getStatus() == null ? 1 : request.getStatus());

        // 3. 先插入 question 主表
        questionMapper.insert(question);

        // 插入后，MyBatis-Plus 会把生成的 ID 回填到 question.id
        Long questionId = question.getId();

        // 4. 如果前端传了选项，就保存到 question_option 表
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (QuestionOptionRequest optionRequest : request.getOptions()) {
                QuestionOption option = new QuestionOption();

                // 关联题目 ID
                option.setQuestionId(questionId);

                // 设置选项 A/B/C/D
                option.setOptionLabel(optionRequest.getOptionLabel());

                // 设置选项内容
                option.setOptionContent(optionRequest.getOptionContent());

                // 设置排序
                option.setSortOrder(optionRequest.getSortOrder());

                questionOptionMapper.insert(option);
            }
        }

        return questionId;
    }

    @Override
    public Boolean updateQuestion(Long id, Question question) {
        Question dbQuestion = questionMapper.selectById(id);

        if (dbQuestion == null) {
            throw new RuntimeException("题目不存在");
        }

        question.setId(id);
        return questionMapper.updateById(question) > 0;
    }

    @Override
    public Boolean deleteQuestion(Long id) {
        Question dbQuestion = questionMapper.selectById(id);

        if (dbQuestion == null) {
            throw new RuntimeException("题目不存在");
        }

        return questionMapper.deleteById(id) > 0;
    }
}