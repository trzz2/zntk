package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zntk.dto.QuestionBatchCreateRequest;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;

    public QuestionServiceImpl(
            QuestionMapper questionMapper,
            QuestionOptionMapper questionOptionMapper
    ) {
        this.questionMapper = questionMapper;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint) {
        LambdaQueryWrapper<Question> wrapper = buildQueryWrapper(questionType, difficulty, knowledgePoint);
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
        Page<Question> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<Question> wrapper = buildQueryWrapper(questionType, difficulty, knowledgePoint);
        wrapper.orderByDesc(Question::getCreateTime);
        return questionMapper.selectPage(page, wrapper);
    }

    @Override
    public QuestionDetailResponse getQuestionById(Long id) {
        Question question = questionMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("Question not found");
        }

        LambdaQueryWrapper<QuestionOption> optionWrapper = new LambdaQueryWrapper<>();
        optionWrapper.eq(QuestionOption::getQuestionId, id);
        optionWrapper.orderByAsc(QuestionOption::getSortOrder);

        QuestionDetailResponse response = new QuestionDetailResponse();
        response.setQuestion(question);
        response.setOptions(questionOptionMapper.selectList(optionWrapper));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuestion(QuestionCreateRequest request) {
        validateQuestionRequest(request);

        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setKnowledgePoint(request.getKnowledgePoint());
        question.setAnswer(request.getAnswer());
        question.setAnalysis(request.getAnalysis());
        question.setStatus(request.getStatus() == null ? StatusEnum.ENABLED.getCode() : request.getStatus());
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        question.setDeleted(0);

        questionMapper.insert(question);
        Long questionId = question.getId();

        saveOptions(questionId, request.getOptions());
        return questionId;
    }

    @Override
    public Boolean updateQuestion(Long id, Question question) {
        if (questionMapper.selectById(id) == null) {
            throw new RuntimeException("Question not found");
        }
        question.setId(id);
        question.setUpdateTime(LocalDateTime.now());
        return questionMapper.updateById(question) > 0;
    }

    @Override
    public Boolean deleteQuestion(Long id) {
        if (questionMapper.selectById(id) == null) {
            throw new RuntimeException("Question not found");
        }
        return questionMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> batchCreate(QuestionBatchCreateRequest request) {
        Set<String> titles = new HashSet<>();
        for (QuestionCreateRequest questionRequest : request.getQuestions()) {
            validateQuestionRequest(questionRequest);
            if (!titles.add(questionRequest.getTitle())) {
                throw new RuntimeException("Duplicate title in batch: " + questionRequest.getTitle());
            }
        }

        List<Long> questionIds = new ArrayList<>();
        for (QuestionCreateRequest questionRequest : request.getQuestions()) {
            questionIds.add(createQuestion(questionRequest));
        }
        return questionIds;
    }

    private LambdaQueryWrapper<Question> buildQueryWrapper(
            Integer questionType,
            Integer difficulty,
            String knowledgePoint
    ) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(questionType != null, Question::getQuestionType, questionType);
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);
        wrapper.like(knowledgePoint != null && !knowledgePoint.isBlank(), Question::getKnowledgePoint, knowledgePoint);
        return wrapper;
    }

    private void saveOptions(Long questionId, List<QuestionOptionRequest> options) {
        if (options == null || options.isEmpty()) {
            return;
        }

        for (QuestionOptionRequest optionRequest : options) {
            QuestionOption option = new QuestionOption();
            option.setQuestionId(questionId);
            option.setOptionLabel(optionRequest.getOptionLabel());
            option.setOptionContent(optionRequest.getOptionContent());
            option.setSortOrder(optionRequest.getSortOrder());
            option.setCreateTime(LocalDateTime.now());
            option.setUpdateTime(LocalDateTime.now());
            option.setDeleted(0);
            questionOptionMapper.insert(option);
        }
    }

    private void validateQuestionRequest(QuestionCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new RuntimeException("Question title cannot be empty");
        }
        if (request.getQuestionType() == null || !QuestionTypeEnum.isValid(request.getQuestionType())) {
            throw new RuntimeException("Question type invalid");
        }
        if (request.getDifficulty() == null || !DifficultyEnum.isValid(request.getDifficulty())) {
            throw new RuntimeException("Difficulty invalid");
        }
        if (request.getStatus() != null && !StatusEnum.isValid(request.getStatus())) {
            throw new RuntimeException("Status invalid");
        }
        if ((request.getQuestionType() == 1 || request.getQuestionType() == 2 || request.getQuestionType() == 3)
                && (request.getOptions() == null || request.getOptions().isEmpty())) {
            throw new RuntimeException("Objective question must have options");
        }
    }
}
