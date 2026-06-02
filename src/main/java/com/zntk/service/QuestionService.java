package com.zntk.service;

import com.zntk.entity.Question;

import java.util.List;

/**
 * 题目业务接口。
 *
 * Controller 不再直接调用 Mapper，
 * 而是调用 Service，由 Service 负责业务逻辑。
 */
public interface QuestionService {

    /**
     * 按条件查询题目列表。
     */
    List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint);

    /**
     * 根据 ID 查询题目详情。
     */
    Question getQuestionById(Long id);

    /**
     * 新增题目，返回生成的题目 ID。
     */
    Long createQuestion(Question question);

    /**
     * 根据 ID 修改题目。
     */
    Boolean updateQuestion(Long id, Question question);

    /**
     * 根据 ID 删除题目。
     */
    Boolean deleteQuestion(Long id);
}