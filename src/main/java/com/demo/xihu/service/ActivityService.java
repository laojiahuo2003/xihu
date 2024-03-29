package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.QueryActivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.vo.ActivityListVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ActivityService extends IService<Activity> {
    public List<Activity> searchByTitle(String title);
    public List<Activity> filterActivities(Date startDate, Date endDate, String location);

    void createActivity(Activity activity);

    Page<Activity> getActivitiesByPage(int pageNo, int pageSize);

    void updateActivity(Activity activity);

    void deleteActivity(Long id);

    List<ActivityListVO> alllist();

    List<ActivityListVO> listByParams(QueryActivitiesDTO queryActivitiesDTO);

    void changeSubCount(Long activityId, int num);

    List<Activity> listById(Integer userid);
}
