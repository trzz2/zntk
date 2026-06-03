package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.entity.WrongQuestion;
import com.zntk.mapper.WrongQuestionMapper;
import com.zntk.service.WrongQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 错题本业务实现类
 *
 * 真正查询 wrong_question 表的逻辑写在这里。
 */
@Service
public class WrongQuestionServiceImpl implements WrongQuestionService {

    /**
     * 错题 Mapper
     *
     * 用来操作 wrong_question 表。
     */
    private final WrongQuestionMapper wrongQuestionMapper;

    /**
     * 构造器注入。
     *
     * Spring 会自动把 WrongQuestionMapper 对象传进来。
     */
    public WrongQuestionServiceImpl(WrongQuestionMapper wrongQuestionMapper) {
        this.wrongQuestionMapper = wrongQuestionMapper;
    }

    @Override
    public List<WrongQuestion> listByUserId(Long userId) {
        // LambdaQueryWrapper 是 MyBatis-Plus 提供的条件构造器。
        // 它的作用是拼接 SQL 查询条件。
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();

        // WHERE user_id = ?
        // 这里的 WrongQuestion::getUserId 是方法引用，
        // MyBatis-Plus 会根据 getUserId 找到数据库字段 user_id。
        wrapper.eq(WrongQuestion::getUserId, userId);

        // ORDER BY last_wrong_time DESC
        // 最近答错的题排在前面，更符合错题本使用习惯。
        wrapper.orderByDesc(WrongQuestion::getLastWrongTime);

        // selectList(wrapper) 表示按照 wrapper 条件查询多条错题记录。
        return wrongQuestionMapper.selectList(wrapper);
    }
}