package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题记录实体类
 *
 * 对应数据库中的 wrong_question 表。
 *
 * 一条记录表示：
 * 某个用户在某次考试中答错了某道题。
 */
@Data
public class WrongQuestion {

    /**
     * 错题记录 ID
     *
     * 使用 MyBatis-Plus 雪花算法自动生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 ID
     *
     * 表示是谁答错了。
     */
    private Long userId;

    /**
     * 题目 ID
     *
     * 表示哪道题答错了。
     */
    private Long questionId;

    /**
     * 试卷 ID
     *
     * 表示这道错题来自哪张试卷。
     */
    private Long paperId;

    /**
     * 考试记录 ID
     *
     * 表示这道错题来自哪一次考试。
     */
    private Long examRecordId;

    /**
     * 用户错误答案
     *
     * 例如正确答案是 A，用户提交了 C，
     * 那这里保存 C。
     */
    private String wrongAnswer;

    /**
     * 正确答案
     *
     * 从 question 表里的 answer 字段拿到。
     */
    private String correctAnswer;

    /**
     * 错误次数
     *
     * 如果同一个用户同一道题多次答错，
     * 后面可以把 wrongCount 加 1。
     */
    private Integer wrongCount;

    /**
     * 最近一次答错时间
     */
    private LocalDateTime lastWrongTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     *
     * 0 表示未删除
     * 1 表示已删除
     */
    @TableLogic
    private Integer deleted;
}