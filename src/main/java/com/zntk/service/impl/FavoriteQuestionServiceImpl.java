package com.zntk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zntk.dto.FavoriteQuestionRequest;
import com.zntk.entity.FavoriteQuestion;
import com.zntk.mapper.FavoriteQuestionMapper;
import com.zntk.service.FavoriteQuestionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏题目业务实现类
 *
 * 真正操作 favorite_question 表的逻辑写在这里。
 */
@Service
public class FavoriteQuestionServiceImpl implements FavoriteQuestionService {

    /**
     * 收藏题目 Mapper
     *
     * 用来操作 favorite_question 表。
     */
    private final FavoriteQuestionMapper favoriteQuestionMapper;

    /**
     * 构造器注入。
     *
     * Spring 会自动把 FavoriteQuestionMapper 传进来。
     */
    public FavoriteQuestionServiceImpl(FavoriteQuestionMapper favoriteQuestionMapper) {
        this.favoriteQuestionMapper = favoriteQuestionMapper;
    }

    @Override
    public Boolean favorite(FavoriteQuestionRequest request) {
        // 查询这个用户和这道题之间是否已经存在收藏记录。
        //
        // 这里要能查到 deleted = 0 和 deleted = 1 两种记录。
        // 所以 FavoriteQuestion 实体类里的 deleted 字段不要加 @TableLogic。
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteQuestion::getUserId, request.getUserId());
        wrapper.eq(FavoriteQuestion::getQuestionId, request.getQuestionId());

        FavoriteQuestion oldFavorite = favoriteQuestionMapper.selectOne(wrapper);

        if (oldFavorite == null) {
            // 情况 1：以前从来没收藏过。
            // 数据库里没有 userId + questionId 这条记录，所以新增。
            FavoriteQuestion favoriteQuestion = new FavoriteQuestion();

            favoriteQuestion.setUserId(request.getUserId());
            favoriteQuestion.setQuestionId(request.getQuestionId());
            favoriteQuestion.setCreateTime(LocalDateTime.now());
            favoriteQuestion.setDeleted(0);

            favoriteQuestionMapper.insert(favoriteQuestion);

            return true;
        }

        if (oldFavorite.getDeleted() == 0) {
            // 情况 2：当前已经收藏着。
            // 不需要重复收藏，直接抛异常提醒前端。
            throw new RuntimeException("题目已收藏，请勿重复收藏");
        }

        // 情况 3：以前收藏过，但后来取消了。
        // 数据库里有这条记录，只是 deleted = 1。
        // 所以不用 insert，直接恢复成 deleted = 0。
        oldFavorite.setDeleted(0);

        favoriteQuestionMapper.updateById(oldFavorite);

        return true;
    }

    @Override
    public List<FavoriteQuestion> listByUserId(Long userId) {
        // 查询当前用户所有未取消收藏的题目。
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(FavoriteQuestion::getUserId, userId);
        wrapper.eq(FavoriteQuestion::getDeleted, 0);
        wrapper.orderByDesc(FavoriteQuestion::getCreateTime);

        return favoriteQuestionMapper.selectList(wrapper);
    }

    @Override
    public Boolean cancel(Long userId, Long questionId) {
        // 根据 userId + questionId 找到收藏记录。
        LambdaQueryWrapper<FavoriteQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteQuestion::getUserId, userId);
        wrapper.eq(FavoriteQuestion::getQuestionId, questionId);

        FavoriteQuestion favoriteQuestion = favoriteQuestionMapper.selectOne(wrapper);

        if (favoriteQuestion == null || favoriteQuestion.getDeleted() == 1) {
            throw new RuntimeException("收藏记录不存在");
        }

        // 不做物理删除，只把 deleted 改成 1。
        favoriteQuestion.setDeleted(1);

        favoriteQuestionMapper.updateById(favoriteQuestion);

        return true;
    }
}