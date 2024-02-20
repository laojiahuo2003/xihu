package com.demo.xihu.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Registration;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.BaseException;
import com.demo.xihu.mapper.ActivityMapper;
import com.demo.xihu.mapper.RegistrationMapper;
import com.demo.xihu.mapper.UserMapper;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.utils.ThreadLocalUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration> implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public void register(RegistrationDTO registrationDTO) {
        //检验活动是否存在
        Long activityId = registrationDTO.getActivityId();
        if(activityMapper.selectById(activityId)==null) throw new BaseException("活动id无效");
        //取出当前登录id
        Map<String,Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        //检验是否重复报名
        QueryWrapper<Registration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("activity_id", activityId);
        List<Registration> registrations = registrationMapper.selectList(queryWrapper);
        if (registrations != null && !registrations.isEmpty()) {
            throw new BaseException("已存在报名信息");
        }
        //操作数据库，插入报名信息
        Registration registration=new Registration();
        BeanUtils.copyProperties(registrationDTO,registration);
        registration.setUserId((long)userId);
        registration.setRegistrationTime(LocalDateTime.now());
        registrationMapper.insert(registration);
    }


    public List<Integer> findSubbyUserId(Integer userId) {
        return registrationMapper.selectSubbyUserId(userId);
    }
}
