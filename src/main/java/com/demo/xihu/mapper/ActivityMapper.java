package com.demo.xihu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.User;
import com.demo.xihu.vo.ActivityListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {



     List<ActivityListVO> selectByParams(LocalDate date, String type, Integer num);

     void changeSubCount(Long activityId, int num);

     List<Activity> selectInfoById(Integer userid);
}
