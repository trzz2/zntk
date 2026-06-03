package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏题目实体类
 *
 * 对应数据库中的 favorite_question 表。
 *
 * 一条记录表示：
 * 某个用户收藏了某道题。
 */
@Data
public class FavoriteQuestion {

    /**
     * 收藏记录 ID
     *
     * 使用 MyBatis-Plus 雪花算法自动生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 ID
     *
     * 表示是谁收藏了题目。
     */
    private Long userId;

    /**
     * 题目 ID
     *
     * 表示收藏了哪道题。
     */
    private Long questionId;

    /**
     * 收藏时间
     */
    private LocalDateTime createTime;

    /**
     * 逻辑删除字段
     *
     * 0 表示已收藏
     * 1 表示已取消收藏
     */

    private Integer deleted;
}