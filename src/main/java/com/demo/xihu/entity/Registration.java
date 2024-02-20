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

//import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("Registration")
public class Registration implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    @NotNull
    private Long id;

    //用户id
    @TableField("user_id")
    @NotNull
    private Long userId;

    //活动id
    @TableField("activity_id")
    @NotNull
    private Long activityId;

    // 注册时间
    @TableField("registration_time")
    @NotNull
    private LocalDateTime registrationTime;

    // 报名渠道
    @TableField("registration_channel")
    @NotNull
    private String registrationChannel;
}
