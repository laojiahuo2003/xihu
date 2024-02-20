package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.User;
import com.demo.xihu.mapper.ActivityMapper;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.vo.ActivityListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {
    @Autowired
    private ActivityMapper activityMapper;


    public List<Activity> searchByTitle(String title) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", title);
        return activityMapper.selectList(queryWrapper);
    }

    public List<Activity> filterActivities(Date startDate, Date endDate, String location) {
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        if (startDate != null) {
            queryWrapper.ge("start_time", startDate);
        }
        if (endDate != null) {
            queryWrapper.le("end_time", endDate);
        }
        if (location != null && !location.isEmpty()) {
            queryWrapper.eq("location", location);
        }
        return activityMapper.selectList(queryWrapper);
    }

    public void createActivity(Activity activity) {
        activityMapper.insert(activity);
    }


    public Page<Activity> getActivitiesByPage(int pageNo, int pageSize) {
        Page<Activity> page = new Page<>(pageNo, pageSize);
        QueryWrapper<Activity> queryWrapper = new QueryWrapper<>();
        // 添加查询条件
        // queryWrapper.eq("some_field", "some_value");
        return this.page(page, queryWrapper);
    }

    public void updateActivity(Activity activity) {
        activityMapper.updateById(activity);
    }

    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    public List<ActivityListVO> alllist() {
        List<Activity> activityList=activityMapper.selectList(null);
        List<ActivityListVO> activityListVO=new ArrayList<>();
        for (Activity activity : activityList) {
            ActivityListVO activityListVOItem = new ActivityListVO();
            // 使用BeanUtils.copyProperties方法复制属性
            BeanUtils.copyProperties(activity, activityListVOItem);
            // 将新创建的ActivityListVO对象添加到列表中
            activityListVO.add(activityListVOItem);
        }
        return activityListVO;
    }

}
