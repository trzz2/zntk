package com.zntk.controller;

import com.zntk.common.Result;
import com.zntk.entity.WrongQuestion;
import com.zntk.service.WrongQuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 错题本接口
 *
 * 负责接收前端请求，返回用户错题数据。
 */
@RestController
public class WrongQuestionController {

    /**
     * 错题本业务对象
     *
     * Controller 不直接操作 Mapper，
     * 而是调用 Service。
     */
    private final WrongQuestionService wrongQuestionService;

    /**
     * 构造器注入。
     *
     * Spring 会自动把 WrongQuestionService 的实现类传进来。
     */
    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }

    /**
     * 查询用户错题列表
     *
     * 请求示例：
     * GET /wrong-questions?userId=1
     */
    @GetMapping("/wrong-questions")
    public Result<List<WrongQuestion>> listByUserId(@RequestParam Long userId) {
        // 调用 Service 查询用户错题。
        List<WrongQuestion> wrongQuestions = wrongQuestionService.listByUserId(userId);

        // 使用统一返回 Result 包装结果。
        return Result.success(wrongQuestions);
    }
}