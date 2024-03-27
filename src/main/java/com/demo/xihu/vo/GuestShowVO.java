package com.demo.xihu.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestShowVO {
    //嘉宾姓名
    private String guestname;

    //描述
    private String description;

    //视频
    private String videoUrl;

    //头像
    private String avatar;

    //是否专家
    private Integer isExpert;


    //参会议程
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
    //会议名称
    private String title;
}
