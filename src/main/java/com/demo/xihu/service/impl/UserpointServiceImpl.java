package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.demo.xihu.entity.User;
import com.demo.xihu.entity.Userpoint;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.mapper.UserpointMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.UserpointService;
import com.demo.xihu.vo.PointsRecordVO;
import com.demo.xihu.vo.PointsStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserpointServiceImpl implements UserpointService {
    @Autowired
    private UserpointMapper userpointMapper;
    @Transactional
    public Result addressPoint(Integer userId, Long pointId, Integer pointnum) {
        //查询用户积分记录
        Userpoint userpoint = userpointMapper.selectOne(new QueryWrapper<Userpoint>()
                .eq("user_id", userId)
                .eq("point_id", pointId));

        LocalDateTime now = LocalDateTime.now();

        if (userpoint == null) {
            // 如果用户积分记录不存在，则新增记录并增加积分
            userpointMapper.insert(new Userpoint(null, userId, pointId, now)); // 插入记录
            return Result.success("参与成功");
        } else {
            // 如果用户积分记录存在，则检查时间间隔
            LocalDateTime updateTime = userpoint.getUpdateTime();
            Duration duration = Duration.between(updateTime, now);
            if (duration.toHours() >= 24) {
                // 如果时间间隔大于等于24小时，则更新时间并增加积分
                userpointMapper.update(new UpdateWrapper<Userpoint>()
                        .eq("user_id", userId)
                        .eq("point_id", pointId)
                        .set("update_time", now));
                return Result.success("参与成功");
            }
        }
        return Result.error("今日已参与");

    }

    @Override
    public List<PointsStatusVO> getPointsStatus(Integer userId) {
        LocalDateTime localDateTime=LocalDateTime.now();
        return userpointMapper.PointsStatusList(userId,localDateTime.minusHours(24));
    }

    @Override
    public List<PointsRecordVO> pointsRecordList(Integer userId) {
        return userpointMapper.userpointList(userId);
    }
}
