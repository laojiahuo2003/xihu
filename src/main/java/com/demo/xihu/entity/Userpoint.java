package com.demo.xihu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("userpoint")
public class Userpoint {
    @TableId(value = "id",type = IdType.AUTO)
    @NotNull
    private Long id;

    //用户id
    @TableField("user_id")
    private Integer userId;

    //活动id
    @TableField("point_id")
    private Long pointId;

    //最新参与时间
    @TableField("update_time")
    private LocalDateTime updateTime;
}
