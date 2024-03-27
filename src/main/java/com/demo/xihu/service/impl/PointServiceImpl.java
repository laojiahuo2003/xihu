package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.entity.Point;
import com.demo.xihu.mapper.PointMapper;
import com.demo.xihu.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointServiceImpl implements PointService {
    @Autowired
    private PointMapper pointMapper;
    @Override
    public Point getbyName(String  pointname) {
        QueryWrapper<Point> pointQueryWrapper = new QueryWrapper<>();
        return pointMapper.selectOne(pointQueryWrapper.eq("name",pointname));
    }
}
