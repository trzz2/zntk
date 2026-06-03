package com.zntk.dto;

import lombok.Data;

/**
 * 考试排行榜返回对象
 *
 * 这个类是 DTO，不是数据库表。
 * 它的作用是：把 Redis 排行榜里的数据整理成前端容易看的格式。
 */
@Data
public class ExamRankingResponse {

    /**
     * 排名
     *
     * 例如：
     * 1 表示第一名
     * 2 表示第二名
     */
    private Integer rank;

    /**
     * 用户 ID
     *
     * Redis ZSet 里保存的 member 是字符串，
     * 例如 "1"、"2"。
     *
     * 后端查出来后，会把它转成 Long 类型的 userId。
     */
    private Long userId;

    /**
     * 分数
     *
     * Redis ZSet 的 score 是 double 类型，
     * 所以这里用 Double 接收。
     */
    private Double score;
}