package com.demo.xihu.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("guest")
public class Guest implements Serializable {
    //主键id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    //嘉宾姓名
    @TableField("guestname")
    private String guestname;

    //描述
    @TableField("description")
    private String description;

    //视频
    @TableField("video_url")
    private String videoUrl;

    //头像
    @TableField("avatar")
    private String avatar;

    //参加的大会id
    @TableField("activity_id")
    private Integer activityId;

    //是否为专家
    @TableField("is_expert")
    private Integer isExpert;

}
