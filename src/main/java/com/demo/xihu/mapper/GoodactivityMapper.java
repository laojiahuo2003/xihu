package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Goodactivity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodactivityMapper extends BaseMapper<Goodactivity> {

    void changeSubCount(Long activityId, int num);

    List<Goodactivity> selectInfoById(Integer userid);
}
