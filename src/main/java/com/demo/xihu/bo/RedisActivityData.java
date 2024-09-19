package com.demo.xihu.bo;

import com.demo.xihu.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisActivityData {
    // 活动列表
    private List<Activity> activityList;
    // 逻辑过期时间
    private LocalDateTime expireTime;
}
