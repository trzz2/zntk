package com.zntk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zntk.common.RequireAdmin;
import com.zntk.common.Result;
import com.zntk.dto.QuestionBatchCreateRequest;
import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.entity.Question;
import com.zntk.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "题目管理", description = "题目查询、分页、新增、修改、删除和批量导入")
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "查询题目列表", description = "支持按题型、难度、知识点进行条件查询")
    @GetMapping
    public Result<List<Question>> list(
            @Parameter(description = "题型：1 单选，2 多选，3 判断，4 简答")
            @RequestParam(required = false) Integer questionType,
            @Parameter(description = "难度：1 简单，2 中等，3 困难")
            @RequestParam(required = false) Integer difficulty,
            @Parameter(description = "知识点关键字")
            @RequestParam(required = false) String knowledgePoint
    ) {
        return Result.success(questionService.listQuestions(questionType, difficulty, knowledgePoint));
    }

    @Operation(summary = "分页查询题目", description = "返回 MyBatis-Plus Page 分页结果")
    @GetMapping("/page")
    public Result<Page<Question>> page(
            @Parameter(description = "页码，从 1 开始")
            @RequestParam(defaultValue = "1") Long pageNo,
            @Parameter(description = "每页数量")
            @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "题型：1 单选，2 多选，3 判断，4 简答")
            @RequestParam(required = false) Integer questionType,
            @Parameter(description = "难度：1 简单，2 中等，3 困难")
            @RequestParam(required = false) Integer difficulty,
            @Parameter(description = "知识点关键字")
            @RequestParam(required = false) String knowledgePoint
    ) {
        return Result.success(questionService.pageQuestions(
                pageNo,
                pageSize,
                questionType,
                difficulty,
                knowledgePoint
        ));
    }

    @Operation(summary = "查询题目详情", description = "返回题目基础信息和选项列表")
    @GetMapping("/{id}")
    public Result<QuestionDetailResponse> getById(@Parameter(description = "题目 ID") @PathVariable Long id) {
        return Result.success(questionService.getQuestionById(id));
    }

    @RequireAdmin
    @Operation(summary = "新增题目", description = "管理员新增题目，同时支持保存题目选项")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody QuestionCreateRequest request) {
        return Result.success(questionService.createQuestion(request));
    }

    @RequireAdmin
    @Operation(summary = "修改题目", description = "管理员根据题目 ID 修改题目基础信息")
    @PutMapping("/{id}")
    public Result<Boolean> update(@Parameter(description = "题目 ID") @PathVariable Long id,
                                  @RequestBody Question question) {
        return Result.success(questionService.updateQuestion(id, question));
    }

    @RequireAdmin
    @Operation(summary = "删除题目", description = "管理员逻辑删除题目")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "题目 ID") @PathVariable Long id) {
        return Result.success(questionService.deleteQuestion(id));
    }

    @RequireAdmin
    @Operation(summary = "批量导入题目", description = "管理员一次性导入多道题目，失败时整体回滚")
    @PostMapping("/batch")
    public Result<List<Long>> batchCreate(@RequestBody @Valid QuestionBatchCreateRequest request) {
        return Result.success(questionService.batchCreate(request));
    }
}
