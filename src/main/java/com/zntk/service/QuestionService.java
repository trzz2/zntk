package com.zntk.service;

import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.entity.Question;

import java.util.List;

public interface QuestionService {

    List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint);

    QuestionDetailResponse getQuestionById(Long id);

    Long createQuestion(QuestionCreateRequest request);

    Boolean updateQuestion(Long id, Question question);

    Boolean deleteQuestion(Long id);
}