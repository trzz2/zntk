package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.common.UserContext;
import com.zntk.dto.ExamHistoryResponse;
import com.zntk.dto.ExamRankingResponse;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitExamRequest;
import com.zntk.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "考试管理", description = "开始考试、提交试卷、查询成绩、历史记录和排行榜")
@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @Operation(summary = "开始考试", description = "为当前登录用户创建考试记录，返回 examRecordId")
    @PostMapping("/start")
    public Result<Long> start(@Valid @RequestBody StartExamRequest request) {
        request.setUserId(UserContext.getUserId());
        return Result.success(examService.startExam(request));
    }

    @Operation(summary = "提交试卷", description = "提交用户答案，后端自动判分、防重复提交、防越权和防超时")
    @PostMapping("/submit")
    public Result<Boolean> submit(@Valid @RequestBody SubmitExamRequest request) {
        return Result.success(examService.submitExam(request));
    }

    @Operation(summary = "查询考试结果", description = "根据考试记录 ID 查询成绩和每道题的作答记录")
    @GetMapping("/{id}")
    public Result<ExamResultResponse> getResult(@Parameter(description = "考试记录 ID") @PathVariable Long id) {
        return Result.success(examService.getExamResult(id));
    }

    @Operation(summary = "查询试卷排行榜", description = "基于 Redis ZSet 按分数从高到低返回排行榜")
    @GetMapping("/ranking")
    public Result<List<ExamRankingResponse>> getRanking(
            @Parameter(description = "试卷 ID") @RequestParam Long paperId,
            @Parameter(description = "返回前 N 名，不传默认 10") @RequestParam(required = false) Integer limit
    ) {
        return Result.success(examService.getRanking(paperId, limit));
    }

    @Operation(summary = "查询我的考试历史", description = "查询当前登录用户的考试记录列表")
    @GetMapping("/history")
    public Result<List<ExamHistoryResponse>> listHistory() {
        return Result.success(examService.listHistory(UserContext.getUserId()));
    }
}
