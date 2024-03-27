package com.demo.xihu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.InfoUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dev-api/registration")
@Slf4j
@Tag(name = "用户_活动相关接口", description = "报名")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ActivityService activityService;

    @PostMapping()
    @Operation(summary = "活动报名")
    public Result registerForActivity(@RequestBody  RegistrationDTO registrationDTO, HttpServletRequest request) {
        log.info("活动信息:{}",registrationDTO);
        String token = request.getHeader("Authorization");
        if(token==null) return Result.error("用户未登录");
        registrationService.register(token,registrationDTO);
        activityService.changeSubCount(registrationDTO.getActivityId(),1);
        return Result.success("报名成功");
    }

    @DeleteMapping("/{cancelActivityId}")
    @Operation(summary = "取消报名")
    public Result cancelRegistration(@PathVariable @NotNull Long cancelActivityId,HttpServletRequest request) {
        log.info("取消活动id信息:{}", cancelActivityId);
        String token = request.getHeader("Authorization");
        if(token==null) return Result.error("用户未登录");
        registrationService.cancelRegistration(token,cancelActivityId);
        activityService.changeSubCount(cancelActivityId,-1);
        return Result.success("取消成功");
    }





}
