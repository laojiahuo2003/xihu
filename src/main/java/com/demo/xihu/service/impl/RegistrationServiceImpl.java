package com.demo.xihu.service.impl;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.demo.xihu.constant.pointName;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.*;
import com.demo.xihu.exception.BaseException;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.mapper.*;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.JwtUtil;
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
    @Autowired
    private PointMapper pointMapper;
    @Autowired
    private UserpointMapper userpointMapper;
    @Autowired
    private UserService userService;
    /**
     * 取消活动
     * @param token
     * @param cancelActivityId
     */
    public void cancelRegistration(String token,Long cancelActivityId) {
        //检验活动是否存在
        if(cancelActivityId==null||activityMapper.selectById(cancelActivityId)==null) throw new BaseException("活动id无效");
        //解析当前登录id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
        }catch (Exception e) {
            throw new UserNotLoginException("token失效,请重新登录");
        }
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        //检验是否重复取消
        QueryWrapper<Registration> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("activity_id", cancelActivityId);
        List<Registration> registrations = registrationMapper.selectList(queryWrapper);
        if (registrations == null || registrations.isEmpty()) {
            throw new BaseException("不存在报名信息");
        }
        //操作数据库，删除报名信息
        QueryWrapper<Registration> registrationQueryWrapper = new QueryWrapper<Registration>().eq("user_id", userId).eq("activity_id", cancelActivityId);
        registrationMapper.delete(registrationQueryWrapper);
    }

    /**
     * 订阅活动
     * @param token
     * @param registrationDTO
     */
    public void register(String token,RegistrationDTO registrationDTO) {
        //检验活动是否存在
        Long activityId = registrationDTO.getActivityId();
        if(activityMapper.selectById(activityId)==null) throw new BaseException("活动id无效");
        //解析当前登录id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
        }catch (Exception e) {
            throw new UserNotLoginException("token失效,请重新登录");
        }
        Map<String, Object> claims = JwtUtil.parseToken(token);
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

        //找积分记录，如果时间符合或者没有记录就增加一条
        Point point = pointMapper.selectOne(new QueryWrapper<Point>().eq("name", pointName.SUBSCRIBE_MEETING));
        Long pointId = point.getId();
        Integer pointnum = point.getPointnum();
        Userpoint userpoints = userpointMapper.selectOne(new QueryWrapper<Userpoint>().eq("user_id", userId).eq("point_id", pointId));
        if(userpoints==null){
            userpointMapper.insert(new Userpoint().builder().userId(userId).pointId(pointId).updateTime(LocalDateTime.now()).build());
            userService.addpoint(userId,pointnum);
        }else if(userpoints.getUpdateTime().isBefore(LocalDateTime.now().minusHours(24))){
            UpdateWrapper<Userpoint> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("user_id",userId).eq("point_id",pointId).set("update_time", LocalDateTime.now());
            userpointMapper.update(null, updateWrapper);
            userService.addpoint(userId,pointnum);
        }
    }


    public List<Integer> findSubbyUserId(Integer userId) {
        return registrationMapper.selectSubbyUserId(userId);
    }

}
