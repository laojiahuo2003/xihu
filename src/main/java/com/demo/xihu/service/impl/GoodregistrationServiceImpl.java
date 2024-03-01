package com.demo.xihu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.dto.GoodregistrationDTO;
import com.demo.xihu.entity.Goodregistration;
import com.demo.xihu.exception.BaseException;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.mapper.GoodactivityMapper;
import com.demo.xihu.mapper.GoodregistrationMapper;
import com.demo.xihu.service.GoodregistrationService;
import com.demo.xihu.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GoodregistrationServiceImpl extends ServiceImpl<GoodregistrationMapper,Goodregistration> implements GoodregistrationService{
    @Autowired
    private GoodregistrationMapper goodregistrationMapper;
    @Autowired
    private GoodactivityMapper goodactivityMapper;

    @Override
    public void register(String token, GoodregistrationDTO goodregistrationDTO) {
        //检验活动是否存在
        Long activityId = goodregistrationDTO.getActivityId();
        if(goodactivityMapper.selectById(activityId)==null) throw new BaseException("活动id无效");
        //解析当前登录id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
        }catch (Exception e) {
            throw new UserNotLoginException("token失效,请重新登录");
        }
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        //检验是否重复报名
        QueryWrapper<Goodregistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("goodactivity_id", activityId);
        List<Goodregistration> registrations = goodregistrationMapper.selectList(queryWrapper);
        if (registrations != null && !registrations.isEmpty()) {
            throw new BaseException("已存在报名信息");
        }
        //操作数据库，插入报名信息
        Goodregistration goodregistration = new Goodregistration();
        BeanUtils.copyProperties(goodregistrationDTO,goodregistration);
        goodregistration.setUserId((long)userId);
        goodregistration.setRegistrationTime(LocalDateTime.now());
        goodregistrationMapper.insert(goodregistration);
    }

    @Override
    public void cancelRegistration(String token, Long cancelActivityId) {
        //检验活动是否存在
        if(cancelActivityId==null||goodactivityMapper.selectById(cancelActivityId)==null) throw new BaseException("活动id无效");
        //解析当前登录id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
        }catch (Exception e) {
            throw new UserNotLoginException("token失效,请重新登录");
        }
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        //检验是否重复取消
        QueryWrapper<Goodregistration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("goodactivity_id", cancelActivityId);
        List<Goodregistration> goodregistrations = goodregistrationMapper.selectList(queryWrapper);
        if (goodregistrations == null || goodregistrations.isEmpty()) {
            throw new BaseException("不存在报名信息");
        }
        //操作数据库，删除报名信息
        QueryWrapper<Goodregistration> goodregistrationQueryWrapper = new QueryWrapper<Goodregistration>().eq("user_id", userId).eq("goodactivity_id", cancelActivityId);
        goodregistrationMapper.delete(goodregistrationQueryWrapper);
    }

    @Override
    public List<Integer> findSubbyUserId(Integer userId) {
        return goodregistrationMapper.selectSubbyUserId(userId);
    }
}
