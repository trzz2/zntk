package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.common.UserContext;
import com.zntk.dto.WrongQuestionDetailResponse;
import com.zntk.service.WrongQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "错题本", description = "查询当前用户的错题归集结果")
@RestController
public class WrongQuestionController {

    private final WrongQuestionService wrongQuestionService;

    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }

    @Operation(summary = "查询我的错题", description = "查询当前登录用户所有错题详情，包括错误次数和最近错误时间")
    @GetMapping("/wrong-questions")
    public Result<List<WrongQuestionDetailResponse>> listByCurrentUser() {
        return Result.success(wrongQuestionService.listByUserId(UserContext.getUserId()));
    }
}
