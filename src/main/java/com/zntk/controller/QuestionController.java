package com.zntk.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zntk.common.Result;
import com.zntk.dto.QuestionCreateRequest;
import com.zntk.dto.QuestionDetailResponse;
import com.zntk.entity.Question;
import com.zntk.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 题目接口控制器。
 *
 * Controller 负责接收 HTTP 请求，
 * 具体业务逻辑交给 QuestionService。
 */
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 构造器注入 QuestionService。
     */
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public Result<List<Question>> list(
            @RequestParam(required = false) Integer questionType,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String knowledgePoint
    ) {
        List<Question> questions = questionService.listQuestions(questionType, difficulty, knowledgePoint);
        return Result.success(questions);
    }

    @GetMapping("/page")
    public Result<Page<Question>> page(
            // 当前页码。
            // 如果前端不传，默认查第 1 页。
            @RequestParam(defaultValue = "1") Long pageNo,

            // 每页条数。
            // 如果前端不传，默认每页 10 条。
            @RequestParam(defaultValue = "10") Long pageSize,

            // 题型筛选条件。
            // required = false 表示不是必传。
            @RequestParam(required = false) Integer questionType,

            // 难度筛选条件。
            @RequestParam(required = false) Integer difficulty,

            // 知识点筛选条件。
            @RequestParam(required = false) String knowledgePoint
    ) {
        // 调用 Service 层分页查询方法
        Page<Question> page = questionService.pageQuestions(
                pageNo,
                pageSize,
                questionType,
                difficulty,
                knowledgePoint
        );

        // 使用统一返回结果包装分页数据
        return Result.success(page);
    }

    @GetMapping("/{id}")
    public Result<QuestionDetailResponse> getById(@PathVariable Long id) {
        // 查询题目详情：题目基础信息 + 选项列表
        QuestionDetailResponse question = questionService.getQuestionById(id);
        return Result.success(question);
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody QuestionCreateRequest request) {
        // @Valid 会触发 QuestionCreateRequest 中的参数校验注解
        // 如果 title、questionType、difficulty 不合法，会在进入 Service 前直接抛出校验异常
        Long id = questionService.createQuestion(request);
        return Result.success(id);
    }

    @PutMapping("/{id}")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Question question) {
        Boolean updated = questionService.updateQuestion(id, question);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean deleted = questionService.deleteQuestion(id);
        return Result.success(deleted);
    }
}