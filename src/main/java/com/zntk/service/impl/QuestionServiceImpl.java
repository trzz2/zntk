package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.entity.Question;
import com.zntk.mapper.QuestionMapper;
import com.zntk.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 题目业务实现类。
 *
 * 这里以后会写真正的业务逻辑：
 * 参数校验、默认值处理、存在性判断、权限判断等。
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    /**
     * 构造器注入 QuestionMapper。
     * Spring 会自动把 QuestionMapper 对象传进来。
     */
    public QuestionServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint) {
        // 创建 MyBatis-Plus 查询条件构造器
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 如果传了题型，就按题型精确查询
        wrapper.eq(questionType != null, Question::getQuestionType, questionType);

        // 如果传了难度，就按难度精确查询
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);

        // 如果传了知识点，就按知识点模糊查询
        wrapper.like(knowledgePoint != null && !knowledgePoint.isBlank(),
                Question::getKnowledgePoint,
                knowledgePoint);

        // 最新创建的题目排在前面
        wrapper.orderByDesc(Question::getCreateTime);

        return questionMapper.selectList(wrapper);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionMapper.selectById(id);
    }

    @Override
    public Long createQuestion(Question question) {
        // 这里先做最基础的非空校验
        if (question.getTitle() == null || question.getTitle().isBlank()) {
            throw new RuntimeException("题干不能为空");
        }

        if (question.getQuestionType() == null) {
            throw new RuntimeException("题型不能为空");
        }

        if (question.getDifficulty() == null) {
            throw new RuntimeException("难度不能为空");
        }

        // 如果前端没有传 status，默认启用
        if (question.getStatus() == null) {
            question.setStatus(1);
        }

        questionMapper.insert(question);

        return question.getId();
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