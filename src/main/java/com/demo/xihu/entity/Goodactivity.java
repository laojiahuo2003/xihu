package com.demo.xihu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("goodactivity")
public class Goodactivity implements Serializable {
    //主键id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    //名称
    @TableField("title")
    @NotEmpty
    private String title;

    //活动类型
    @TableField("type")
    private String Type;

    //活动图片
    @TableField("image")
    private String image;

    //时间
    @TableField("time")
    private String time;


    //描述
    @TableField("content")
    private String content;

    //订阅数
    @TableField("peo")
    private Integer peo;

    @TableField(exist = false)  // 表示这个字段不是数据库表的一部分
    private Integer isSub=0;




}
