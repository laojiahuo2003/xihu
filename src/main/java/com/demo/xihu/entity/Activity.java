package com.demo.xihu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 3L;

    //主键id
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    //名称
    @TableField("title")
    @NotEmpty
    private String title;

    //活动类型
    @TableField("activity_type")
    private String activityType;

    //活动图片
    @TableField("image_url")
    private String imageUrl;

    //开始时间
    @TableField("start_time")
    private LocalDateTime startTime;

    //结束时间
    @TableField("end_time")
    private LocalDateTime endTime;

    //地点
    @TableField("location")
    private String location;

    //描述
    @TableField("description")
    private String description;

    //直播链接
    @TableField("live_url")
    private String liveUrl;

    //回放链接
    @TableField("replay_url")
    private String replayUrl;

    //订阅总数
    @TableField("subscription_count")
    private int subscriptionCount = 0;

    //热度
    @TableField("popularity")
    private int popularity = 0;

}