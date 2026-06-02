package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 考试记录实体类。
 *
 * 对应 exam_record 表。
 * 表示某个用户参加某张试卷的一次考试。
 */
@Data
public class ExamRecord {

    /**
     * 考试记录 ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 试卷 ID。
     */
    private Long paperId;

    /**
     * 用户 ID。
     */
    private Long userId;

    /**
     * 试卷总分。
     */
    private Integer totalScore;

    /**
     * 用户得分。
     */
    private Integer userScore;

    /**
     * 状态：0-进行中，1-已提交。
     */
    private Integer status;

    /**
     * 开始时间。
     */
    private LocalDateTime startTime;

    /**
     * 提交时间。
     */
    private LocalDateTime submitTime;

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