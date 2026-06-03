package com.zntk.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试历史返回对象
 *
 * 这个 DTO 专门返回给前端展示考试历史。
 * 它不是数据库表。
 */
@Data
public class ExamHistoryResponse {

    /**
     * 考试记录 ID
     */
    private Long examRecordId;

    /**
     * 试卷 ID
     */
    private Long paperId;

    /**
     * 试卷标题
     *
     * 从 paper 表查询出来。
     */
    private String paperTitle;

    /**
     * 试卷总分
     */
    private Integer totalScore;

    /**
     * 用户得分
     */
    private Integer userScore;

    /**
     * 考试状态
     *
     * 0 进行中
     * 1 已提交
     */
    private Integer status;

    /**
     * 开始考试时间
     */
    private LocalDateTime startTime;

    /**
     * 提交考试时间
     */
    private LocalDateTime submitTime;
}