package com.zntk.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.entity.Question;
import com.zntk.mapper.QuestionMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionMapper questionMapper;

    public QuestionController(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @GetMapping
    public List<Question> list(
            // required = false 表示这个请求参数不是必填
            // 例如：/questions 可以不传 questionType
            // 也可以传：/questions?questionType=1
            @RequestParam(required = false) Integer questionType,

            // difficulty 表示题目难度
            // 例如：/questions?difficulty=2 表示查询中等难度的题目
            @RequestParam(required = false) Integer difficulty,

            // knowledgePoint 表示知识点
            // 例如：/questions?knowledgePoint=Redis 表示查询知识点里包含 Redis 的题目
            @RequestParam(required = false) String knowledgePoint
    ) {
        // LambdaQueryWrapper 是 MyBatis-Plus 提供的“查询条件构造器”
        // 可以理解成：用 Java 代码拼 SQL 的 where 条件
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        // 如果 questionType 不为空，就添加条件：
        // question_type = questionType
        // 例如 questionType = 1，相当于 SQL：where question_type = 1
        wrapper.eq(questionType != null, Question::getQuestionType, questionType);

        // 如果 difficulty 不为空，就添加条件：
        // difficulty = difficulty
        // 例如 difficulty = 2，相当于 SQL：where difficulty = 2
        wrapper.eq(difficulty != null, Question::getDifficulty, difficulty);

        // 如果 knowledgePoint 不为空，并且不是空白字符串，就添加模糊查询条件：
        // knowledge_point like '%knowledgePoint%'
        // 例如 knowledgePoint = "Redis"
        // 相当于 SQL：where knowledge_point like '%Redis%'
        wrapper.like(
                knowledgePoint != null && !knowledgePoint.isBlank(),
                Question::getKnowledgePoint,
                knowledgePoint
        );

        // 按创建时间倒序排序
        // 相当于 SQL：order by create_time desc
        // 最新创建的题目会排在最前面
        wrapper.orderByDesc(Question::getCreateTime);

        // 按照上面 wrapper 里拼好的条件查询题目列表
        // 如果没有传任何参数，就是查询全部未逻辑删除的题目
        return questionMapper.selectList(wrapper);
    }
    @GetMapping("/{id}")
    public Question getById(@PathVariable Long id) {
        return questionMapper.selectById(id);
    }

    @PostMapping
    public Long create(@RequestBody Question question) {
        questionMapper.insert(question);
        return question.getId();
    }

    @PutMapping("/{id}")
    public Boolean update(@PathVariable Long id, @RequestBody Question question) {
        question.setId(id);
        return questionMapper.updateById(question) > 0;
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id) {
        return questionMapper.deleteById(id) > 0;
    }
}