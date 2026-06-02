package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.dto.QuestionOptionRequest;
import com.zntk.entity.Question;
import com.zntk.entity.QuestionOption;
import com.zntk.enums.DifficultyEnum;
import com.zntk.enums.QuestionTypeEnum;
import com.zntk.enums.StatusEnum;
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
    public Page<Question> pageQuestions(
            Long pageNo,
            Long pageSize,
            Integer questionType,
            Integer difficulty,
            String knowledgePoint
    ) {
        // 创建分页对象。
        //
        // Page<Question> 表示：
        // 我要分页查询 Question 类型的数据。
        //
        // pageNo：当前第几页
        // pageSize：每页多少条
        //
        // 例如 new Page<>(1, 10)
        // 表示查询第 1 页，每页 10 条。
        Page<Question> page = new Page<>(pageNo, pageSize);

        // 创建查询条件构造器。
        //
        // LambdaQueryWrapper<Question> 表示：
        // 我要为 Question 表拼接查询条件。
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 如果 questionType 不为空，就添加题型筛选条件。
        //
        // 相当于 SQL：
        // where question_type = questionType
        wrapper.eq(questionType != null, Question::getQuestionType, questionType);

        // 如果 difficulty 不为空，就添加难度筛选条件。
        //
        // 相当于 SQL：
        // where difficulty = difficulty
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);

        // 如果 knowledgePoint 不为空并且不是空白字符串，
        // 就添加知识点模糊查询条件。
        //
        // 相当于 SQL：
        // where knowledge_point like '%Redis%'
        wrapper.like(
                knowledgePoint != null && !knowledgePoint.isBlank(),
                Question::getKnowledgePoint,
                knowledgePoint
        );

        // 按创建时间倒序。
        //
        // 相当于 SQL：
        // order by create_time desc
        //
        // 最新创建的题目排在最前面。
        wrapper.orderByDesc(Question::getCreateTime);

        // 执行分页查询。
        //
        // selectPage 是 MyBatis-Plus BaseMapper 提供的方法。
        //
        // 第 1 个参数 page：
        // 告诉 MyBatis-Plus 当前第几页、每页多少条。
        //
        // 第 2 个参数 wrapper：
        // 告诉 MyBatis-Plus 按什么条件查询。
        //
        // 返回值仍然是 Page<Question>，
        // 里面会包含：
        // records：当前页数据
        // total：总记录数
        // current：当前页
        // size：每页条数
        // pages：总页数
        return questionMapper.selectPage(page, wrapper);
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


        // 校验题型是否在系统支持范围内
        if (!QuestionTypeEnum.isValid(request.getQuestionType())) {
            throw new RuntimeException("题型不合法");
        }

// 校验难度是否在系统支持范围内
        if (!DifficultyEnum.isValid(request.getDifficulty())) {
            throw new RuntimeException("难度不合法");
        }

// 如果前端传了 status，就校验 status 是否合法
        if (request.getStatus() != null && !StatusEnum.isValid(request.getStatus())) {
            throw new RuntimeException("状态不合法");
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