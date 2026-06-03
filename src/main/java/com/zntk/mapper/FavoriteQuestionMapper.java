package com.zntk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zntk.entity.FavoriteQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏题目 Mapper
 *
 * 继承 BaseMapper 后，MyBatis-Plus 会自动提供：
 * insert、selectList、selectOne、updateById、deleteById 等方法。
 */
@Mapper
public interface FavoriteQuestionMapper extends BaseMapper<FavoriteQuestion> {
}