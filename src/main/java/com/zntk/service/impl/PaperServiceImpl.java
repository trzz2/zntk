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

@Service
public class PaperServiceImpl implements PaperService {

    private final PaperMapper paperMapper;
    private final PaperQuestionMapper paperQuestionMapper;
    private final QuestionMapper questionMapper;

    public PaperServiceImpl(
            PaperMapper paperMapper,
            PaperQuestionMapper paperQuestionMapper,
            QuestionMapper questionMapper
    ) {
        this.paperMapper = paperMapper;
        this.paperQuestionMapper = paperQuestionMapper;
        this.questionMapper = questionMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPaper(PaperCreateRequest request) {
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new RuntimeException("Paper question list cannot be empty");
        }

        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setDescription(request.getDescription());
        paper.setDurationMinutes(request.getDurationMinutes());
        paper.setStatus(request.getStatus() == null ? StatusEnum.ENABLED.getCode() : request.getStatus());
        paper.setTotalScore(0);
        paper.setCreateTime(LocalDateTime.now());
        paper.setUpdateTime(LocalDateTime.now());
        paper.setDeleted(0);
        paperMapper.insert(paper);

        int totalScore = 0;
        int sortOrder = 1;
        for (PaperQuestionRequest questionRequest : request.getQuestions()) {
            Question question = questionMapper.selectById(questionRequest.getQuestionId());
            if (question == null) {
                throw new RuntimeException("Question not found: " + questionRequest.getQuestionId());
            }

            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paper.getId());
            paperQuestion.setQuestionId(questionRequest.getQuestionId());
            paperQuestion.setScore(questionRequest.getScore());
            paperQuestion.setSortOrder(questionRequest.getSortOrder() == null ? sortOrder : questionRequest.getSortOrder());
            paperQuestion.setCreateTime(LocalDateTime.now());
            paperQuestion.setDeleted(0);
            paperQuestionMapper.insert(paperQuestion);

            totalScore += questionRequest.getScore();
            sortOrder++;
        }

        paper.setTotalScore(totalScore);
        paper.setUpdateTime(LocalDateTime.now());
        paperMapper.updateById(paper);
        return paper.getId();
    }

    @Override
    public PaperDetailResponse getPaperById(Long id) {
        Paper paper = paperMapper.selectById(id);
        if (paper == null) {
            throw new RuntimeException("Paper not found");
        }

        LambdaQueryWrapper<PaperQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperQuestion::getPaperId, id);
        wrapper.orderByAsc(PaperQuestion::getSortOrder);

        List<Question> questions = new ArrayList<>();
        for (PaperQuestion paperQuestion : paperQuestionMapper.selectList(wrapper)) {
            Question question = questionMapper.selectById(paperQuestion.getQuestionId());
            if (question != null) {
                questions.add(question);
            }
        }

        PaperDetailResponse response = new PaperDetailResponse();
        response.setPaper(paper);
        response.setQuestions(questions);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long randomPaper(RandomPaperRequest request) {
        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();

        if (request.getQuestionTypes() != null && !request.getQuestionTypes().isEmpty()) {
            questionWrapper.in(Question::getQuestionType, request.getQuestionTypes());
        } else if (request.getQuestionType() != null) {
            questionWrapper.eq(Question::getQuestionType, request.getQuestionType());
        }

        if (request.getDifficulties() != null && !request.getDifficulties().isEmpty()) {
            questionWrapper.in(Question::getDifficulty, request.getDifficulties());
        } else if (request.getDifficulty() != null) {
            questionWrapper.eq(Question::getDifficulty, request.getDifficulty());
        }

        questionWrapper.like(Question::getKnowledgePoint, request.getKnowledgePoint());
        questionWrapper.eq(Question::getStatus, StatusEnum.ENABLED.getCode());

        List<Question> questions = questionMapper.selectList(questionWrapper);
        if (questions.size() < request.getQuestionCount()) {
            throw new RuntimeException("Not enough questions for random paper");
        }

        Collections.shuffle(questions);
        List<Question> selectedQuestions = questions.subList(0, request.getQuestionCount());

        Paper paper = new Paper();
        paper.setTitle(request.getTitle());
        paper.setDescription(request.getDescription());
        paper.setDurationMinutes(request.getDurationMinutes());
        paper.setTotalScore(request.getQuestionCount() * request.getScorePerQuestion());
        paper.setStatus(StatusEnum.ENABLED.getCode());
        paper.setCreateTime(LocalDateTime.now());
        paper.setUpdateTime(LocalDateTime.now());
        paper.setDeleted(0);
        paperMapper.insert(paper);

        int sortOrder = 1;
        for (Question question : selectedQuestions) {
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paper.getId());
            paperQuestion.setQuestionId(question.getId());
            paperQuestion.setScore(request.getScorePerQuestion());
            paperQuestion.setSortOrder(sortOrder++);
            paperQuestion.setCreateTime(LocalDateTime.now());
            paperQuestion.setDeleted(0);
            paperQuestionMapper.insert(paperQuestion);
        }

        return paper.getId();
    }
}
