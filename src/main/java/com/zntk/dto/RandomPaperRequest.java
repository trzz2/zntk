package com.zntk.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 随机组卷请求对象
 *
 * 前端调用随机组卷接口时，会把组卷条件传给这个类。
 */
@Data
public class RandomPaperRequest {

    /**
     * 试卷标题
     */
    @NotBlank(message = "试卷标题不能为空")
    private String title;

    /**
     * 试卷描述
     */
    private String description;

    /**
     * 考试时长，单位分钟
     */
    @NotNull(message = "考试时长不能为空")
    @Min(value = 1, message = "考试时长必须大于 0")
    private Integer durationMinutes;

    /**
     * 题型
     *
     * 1 单选
     * 2 多选
     * 3 判断
     * 4 简答
     */
    @NotNull(message = "题型不能为空")
    private Integer questionType;

    /**
     * 难度
     *
     * 1 简单
     * 2 中等
     * 3 困难
     */
    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    /**
     * 知识点
     *
     * 例如 Redis、Spring Boot、MyBatis-Plus。
     */
    @NotBlank(message = "知识点不能为空")
    private String knowledgePoint;

    /**
     * 题目数量
     *
     * 表示要随机抽多少道题。
     */
    @NotNull(message = "题目数量不能为空")
    @Min(value = 1, message = "题目数量必须大于 0")
    private Integer questionCount;

    /**
     * 每道题分数
     */
    @NotNull(message = "每题分数不能为空")
    @Min(value = 1, message = "每题分数必须大于 0")
    private Integer scorePerQuestion;
}
