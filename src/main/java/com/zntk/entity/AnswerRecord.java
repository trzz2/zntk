package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题记录实体类。
 *
 * 对应 answer_record 表。
 * 表示某次考试中，某道题的作答情况。
 */
@Data
public class AnswerRecord {

    /**
     * 答题记录 ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 考试记录 ID。
     */
    private Long examRecordId;

    /**
     * 题目 ID。
     */
    private Long questionId;

    /**
     * 用户答案。
     */
    private String userAnswer;

    /**
     * 正确答案。
     */
    private String correctAnswer;

    /**
     * 是否正确：0-错误，1-正确。
     */
    private Integer isCorrect;

    /**
     * 本题得分。
     */
    private Integer score;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除。
     */
    @TableLogic
    private Integer deleted;
}