package com.zntk.service;

import com.zntk.dto.FavoriteQuestionRequest;
import com.zntk.entity.FavoriteQuestion;

import java.util.List;

/**
 * 收藏题目业务接口
 *
 * 定义收藏题目的核心业务能力。
 */
public interface FavoriteQuestionService {

    /**
     * 收藏题目
     *
     * @param request 收藏请求，里面包含 userId 和 questionId
     * @return 是否收藏成功
     */
    Boolean favorite(FavoriteQuestionRequest request);

    /**
     * 查询某个用户的收藏题目列表
     *
     * @param userId 用户 ID
     * @return 收藏记录列表
     */
    List<FavoriteQuestion> listByUserId(Long userId);

    /**
     * 取消收藏题目
     *
     * @param userId 用户 ID
     * @param questionId 题目 ID
     * @return 是否取消成功
     */
    Boolean cancel(Long userId, Long questionId);
}