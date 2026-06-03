package com.zntk.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 错题详情返回对象
 *
 * 这个 DTO 专门返回给前端展示错题详情。
 * 它不是数据库表。
 */
@Data
public class WrongQuestionDetailResponse {

    /**
     * 错题记录 ID
     */
    private Long wrongQuestionId;

    /**
     * 题目 ID
     */
    private Long questionId;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题型
     *
     * 1 单选
     * 2 多选
     * 3 判断
     * 4 简答
     */
    private Integer questionType;

    /**
     * 难度
     *
     * 1 简单
     * 2 中等
     * 3 困难
     */
    private Integer difficulty;

    /**
     * 知识点
     */
    private String knowledgePoint;

    /**
     * 题目选项列表
     *
     * 选择题、判断题会有选项。
     * 简答题可以为空列表。
     */
    private List<QuestionOptionRequest> options;

    /**
     * 用户错误答案
     */
    private String wrongAnswer;

    /**
     * 正确答案
     */
    private String correctAnswer;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 最近一次答错时间
     */
    private LocalDateTime lastWrongTime;
}