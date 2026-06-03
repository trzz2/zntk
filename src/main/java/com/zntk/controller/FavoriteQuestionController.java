package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.dto.FavoriteQuestionRequest;
import com.zntk.entity.FavoriteQuestion;
import com.zntk.service.FavoriteQuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏题目接口
 *
 * 负责接收前端的收藏、查询收藏、取消收藏请求。
 */
@RestController
public class FavoriteQuestionController {

    /**
     * 收藏题目业务对象
     *
     * Controller 不直接操作数据库，
     * 而是调用 Service。
     */
    private final FavoriteQuestionService favoriteQuestionService;

    /**
     * 构造器注入。
     *
     * Spring 会自动把 FavoriteQuestionService 的实现类传进来。
     */
    public FavoriteQuestionController(FavoriteQuestionService favoriteQuestionService) {
        this.favoriteQuestionService = favoriteQuestionService;
    }

    /**
     * 收藏题目
     *
     * 请求示例：
     * POST /favorite-questions
     *
     * Body:
     * {
     *   "userId": 1,
     *   "questionId": 1001
     * }
     */
    @PostMapping("/favorite-questions")
    public Result<Boolean> favorite(@RequestBody @Valid FavoriteQuestionRequest request) {
        Boolean result = favoriteQuestionService.favorite(request);
        return Result.success(result);
    }

    /**
     * 查询我的收藏题目
     *
     * 请求示例：
     * GET /favorite-questions?userId=1
     */
    @GetMapping("/favorite-questions")
    public Result<List<FavoriteQuestion>> listByUserId(@RequestParam Long userId) {
        List<FavoriteQuestion> favoriteQuestions = favoriteQuestionService.listByUserId(userId);
        return Result.success(favoriteQuestions);
    }

    /**
     * 取消收藏题目
     *
     * 请求示例：
     * DELETE /favorite-questions?userId=1&questionId=1001
     */
    @DeleteMapping("/favorite-questions")
    public Result<Boolean> cancel(
            @RequestParam Long userId,
            @RequestParam Long questionId
    ) {
        Boolean result = favoriteQuestionService.cancel(userId, questionId);
        return Result.success(result);
    }
}