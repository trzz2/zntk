package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.dto.ExamResultResponse;
import com.zntk.dto.StartExamRequest;
import com.zntk.dto.SubmitExamRequest;
import com.zntk.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
}