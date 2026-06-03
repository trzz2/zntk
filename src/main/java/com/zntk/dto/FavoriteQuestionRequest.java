package com.zntk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 收藏题目请求对象
 *
 * 用来接收前端传来的 JSON 参数。
 */
@Data
public class FavoriteQuestionRequest {

    /**
     * 用户 ID
     *
     * 表示是谁要收藏题目。
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 题目 ID
     *
     * 表示要收藏哪道题。
     */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;
}