package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Goodactivity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodactivityMapper extends BaseMapper<Goodactivity> {

    void changeSubCount(Long activityId, int num);
}
