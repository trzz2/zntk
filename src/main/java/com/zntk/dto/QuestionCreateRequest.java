package com.zntk.dto;

import lombok.Data;

import java.util.List;

/**
 * 新增题目请求对象。
 *
 * Controller 接收前端 JSON 时使用这个类，
 * 而不是直接使用 Question 实体类。
 */
@Data
public class QuestionCreateRequest {

    /**
     * 题干。
     */
    private String title;

    /**
     * 题型：1-单选，2-多选，3-判断，4-简答。
     */
    private Integer questionType;

    /**
     * 难度：1-简单，2-中等，3-困难。
     */
    private Integer difficulty;

    /**
     * 知识点。
     */
    private String knowledgePoint;

    /**
     * 答案。
     * 单选题例如 A，多选题例如 A,B。
     */
    private String answer;

    /**
     * 解析。
     */
    private String analysis;

    /**
     * 状态：0-禁用，1-启用。
     */
    private Integer status;

    /**
     * 题目选项列表。
     * 单选和多选题会使用。
     */
    private List<QuestionOptionRequest> options;
}