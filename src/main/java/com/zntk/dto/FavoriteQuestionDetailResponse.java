package com.zntk.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏题目详情返回对象
 *
 * 用来返回给前端展示“我的收藏题目”。
 * 它不是数据库表。
 */
@Data
public class FavoriteQuestionDetailResponse {

    /**
     * 收藏记录 ID
     */
    private Long favoriteQuestionId;

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
     */
    private List<QuestionOptionRequest> options;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;
}