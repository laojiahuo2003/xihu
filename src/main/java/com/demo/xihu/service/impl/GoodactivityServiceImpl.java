package com.demo.xihu.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.dto.QueryGoodactivitiesDTO;
import com.demo.xihu.entity.Goodactivity;
import com.demo.xihu.mapper.ActivityMapper;
import com.demo.xihu.mapper.GoodactivityMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.GoodactivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class GoodactivityServiceImpl extends ServiceImpl<GoodactivityMapper, Goodactivity> implements GoodactivityService {
    @Autowired
    private GoodactivityMapper goodactivityMapper;

    @Override
    public Page<Goodactivity> getActivitiesByPage(QueryGoodactivitiesDTO queryGoodactivitiesDTO) {
        Page<Goodactivity> page = new Page<>(queryGoodactivitiesDTO.getStartIndex(),queryGoodactivitiesDTO.getPageSize());
        QueryWrapper<Goodactivity> queryWrapper = new QueryWrapper<>();
        if(!Objects.equals(queryGoodactivitiesDTO.getType(), "全部") &&queryGoodactivitiesDTO.getType()!=null&&!queryGoodactivitiesDTO.getType().isEmpty()){
            queryWrapper.eq("type",queryGoodactivitiesDTO.getType());   //非空就条件查询
        }
        Page<Goodactivity> res = page(page, queryWrapper);


        return res;
    }

    @Override
    public void changeSubCount(Long activityId, int num) {
        goodactivityMapper.changeSubCount(activityId,num);
    }

}
