package com.zntk.service;

import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.entity.Question;

import java.util.List;

/**
 * 题目业务接口。
 *
 * Controller 不直接操作 Mapper，
 * 而是调用 Service 完成业务逻辑。
 */
public interface QuestionService {

    /**
     * 按条件查询题目列表。
     */
    List<Question> listQuestions(Integer questionType, Integer difficulty, String knowledgePoint);

    /**
     * 根据 ID 查询题目详情。
     *
     * 返回题目基础信息 + 选项列表。
     */
    QuestionDetailResponse getQuestionById(Long id);

    /**
     * 新增题目。
     *
     * 支持同时保存题目基础信息和选项列表。
     */
    Long createQuestion(QuestionCreateRequest request);

    /**
     * 根据 ID 修改题目。
     */
    Boolean updateQuestion(Long id, Question question);

    /**
     * 根据 ID 删除题目。
     */
    Boolean deleteQuestion(Long id);
}