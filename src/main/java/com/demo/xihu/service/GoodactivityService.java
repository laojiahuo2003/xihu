package com.demo.xihu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.demo.xihu.dto.QueryGoodactivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Goodactivity;

import java.util.List;

public interface GoodactivityService extends IService<Goodactivity> {
    Page<Goodactivity> getActivitiesByPage(QueryGoodactivitiesDTO queryGoodactivitiesDTO);

    void changeSubCount(Long activityId, int num);

    List<Goodactivity> listById(Integer userid);
}
