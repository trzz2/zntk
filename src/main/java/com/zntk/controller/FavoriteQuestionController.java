package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.common.UserContext;
import com.zntk.dto.FavoriteQuestionDetailResponse;
import com.zntk.dto.FavoriteQuestionRequest;
import com.zntk.service.FavoriteQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "收藏题目", description = "收藏题目、取消收藏和查询我的收藏")
@RestController
public class FavoriteQuestionController {

    private final FavoriteQuestionService favoriteQuestionService;

    public FavoriteQuestionController(FavoriteQuestionService favoriteQuestionService) {
        this.favoriteQuestionService = favoriteQuestionService;
    }

    @Operation(summary = "收藏题目", description = "当前登录用户收藏指定题目")
    @PostMapping("/favorite-questions")
    public Result<Boolean> favorite(@RequestBody @Valid FavoriteQuestionRequest request) {
        request.setUserId(UserContext.getUserId());
        return Result.success(favoriteQuestionService.favorite(request));
    }

    @Operation(summary = "查询我的收藏", description = "查询当前登录用户收藏过的题目详情")
    @GetMapping("/favorite-questions")
    public Result<List<FavoriteQuestionDetailResponse>> listByCurrentUser() {
        return Result.success(favoriteQuestionService.listByUserId(UserContext.getUserId()));
    }

    @Operation(summary = "取消收藏", description = "当前登录用户取消收藏指定题目")
    @DeleteMapping("/favorite-questions")
    public Result<Boolean> cancel(@Parameter(description = "题目 ID") @RequestParam Long questionId) {
        return Result.success(favoriteQuestionService.cancel(UserContext.getUserId(), questionId));
    }
}
