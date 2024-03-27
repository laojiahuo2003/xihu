package com.demo.xihu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("point")
public class Point {
    @TableId(value = "id",type = IdType.AUTO)
    @NotNull
    private Long id;

    //活动名
    @TableField("name")
    private String name;

    //积分数量
    @TableField("pointnum")
    private Integer pointnum;
}
