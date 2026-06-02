package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.entity.Question;
import com.zntk.service.QuestionService;
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

    @GetMapping("/{id}")
    public Result<Question> getById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return Result.success(question);
    }

    @PostMapping
    public Result<Long> create(@RequestBody Question question) {
        Long id = questionService.createQuestion(question);
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