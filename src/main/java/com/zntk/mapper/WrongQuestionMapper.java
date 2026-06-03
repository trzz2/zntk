package com.zntk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zntk.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 错题记录 Mapper
 *
 * 继承 BaseMapper 后，MyBatis-Plus 会自动提供：
 * insert、selectById、selectList、updateById、deleteById 等方法。
 */
@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {
}