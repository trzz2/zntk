package com.zntk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteQuestion {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long questionId;

    private LocalDateTime createTime;

    private Integer deleted;
}
