package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.dto.ExamRankingResponse;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitExamRequest;
import com.zntk.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试接口控制器。
 */
@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    /**
     * 开始考试。
     */
    @PostMapping("/start")
    public Result<Long> start(@Valid @RequestBody StartExamRequest request) {
        Long id = examService.startExam(request);
        return Result.success(id);
    }

    /**
     * 提交考试。
     */
    @PostMapping("/submit")
    public Result<Boolean> submit(@Valid @RequestBody SubmitExamRequest request) {
        Boolean submitted = examService.submitExam(request);
        return Result.success(submitted);
    }

    /**
     * 查询考试结果。
     */
    @GetMapping("/{id}")
    public Result<ExamResultResponse> getResult(@PathVariable Long id) {
        ExamResultResponse result = examService.getExamResult(id);
        return Result.success(result);
    }





    /**
     * 查询某张试卷的成绩排行榜
     *
     * 请求示例：
     * GET /exams/ranking?paperId=3001&limit=10
     *
     * paperId 表示查哪张试卷的排行榜。
     * limit 表示查前多少名。
     */
    @GetMapping("/ranking")
    public Result<List<ExamRankingResponse>> getRanking(
            @RequestParam Long paperId,
            @RequestParam(required = false) Integer limit
    ) {
        // Controller 只负责接收请求参数，然后调用 Service。
        // 真正查询 Redis 排行榜的逻辑在 examService.getRanking 里面。
        List<ExamRankingResponse> rankingList = examService.getRanking(paperId, limit);

        // 使用统一返回 Result，把排行榜列表返回给前端。
        return Result.success(rankingList);
    }



}