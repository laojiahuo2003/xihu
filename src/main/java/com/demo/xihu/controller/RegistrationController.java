package com.demo.xihu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.constant.CacheConstant;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.InfoUserVO;
import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/registration")
@Slf4j
@Tag(name = "用户_活动相关接口", description = "报名")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private Cache<String, Object> cache;
    @PostMapping()
    @Operation(summary = "活动报名")
    public Result registerForActivity(@RequestBody  RegistrationDTO registrationDTO) {
        log.info("活动信息:{}",registrationDTO);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer)claims.get("id");
        registrationService.register(id,registrationDTO);
        activityService.changeSubCount(registrationDTO.getActivityId(),1);
        // 清除缓存
        String key = CacheConstant.ACTIVITY_REGISTER + id;
        cache.invalidate(key);
        log.info("本地缓存清除成功");
        redisService.remove(key);
        log.info("redis缓存清除成功");
        return Result.success("报名成功");
    }


    @DeleteMapping("/{cancelActivityId}")
    @Operation(summary = "取消报名")
    public Result cancelRegistration(@PathVariable @NotNull Long cancelActivityId) {
        log.info("取消活动id信息:{}", cancelActivityId);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer)claims.get("id");
        registrationService.cancelRegistration(id,cancelActivityId);
        activityService.changeSubCount(cancelActivityId,-1);
        // 清除缓存
        String key = CacheConstant.ACTIVITY_REGISTER + id;
        cache.invalidate(key);
        log.info("本地缓存清除成功");
        redisService.remove(key);
        log.info("redis缓存清除成功");
        return Result.success("取消成功");
    }





}
