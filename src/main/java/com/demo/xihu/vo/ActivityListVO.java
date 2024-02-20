package com.demo.xihu.vo;

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
public class ActivityListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键id
    private Long id;

    //名称
    @NotEmpty
    private String title;

    //开始时间
    private LocalDateTime startTime;

    //结束时间
    private LocalDateTime endTime;

    //地点
    private String location;

    //描述
    private String description;

    //直播链接
    private String liveUrl;

    //回放链接
    private String replayUrl;

    //订阅总数
    private int subscriptionCount = 0;

    //热度
    private int popularity = 0;

    //当前用户是否订阅
    private int isSub = 0;
}