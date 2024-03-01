package com.demo.xihu.controller;

import com.demo.xihu.dto.GoodregistrationDTO;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.GoodactivityService;
import com.demo.xihu.service.GoodregistrationService;
import com.demo.xihu.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev-api/goodregistration")
@Slf4j
@Tag(name = "用户_精彩活动相关接口", description = "报名")
public class GoodregistrationController {
    @Autowired
    private GoodregistrationService goodregistrationService;
    @Autowired
    private GoodactivityService goodactivityService;

    @PostMapping()
    @Operation(summary = "精彩活动报名")
    public Result registerForActivity(@RequestBody GoodregistrationDTO goodregistrationDTO, HttpServletRequest request) {
        log.info("活动信息:{}",goodregistrationDTO);
        String token = request.getHeader("Authorization");
        if(token==null) return Result.error("用户未登录");
        goodregistrationService.register(token,goodregistrationDTO);
        goodactivityService.changeSubCount(goodregistrationDTO.getActivityId(),1);
        return Result.success("报名成功");
    }

    @DeleteMapping("/{cancelActivityId}")
    @Operation(summary = "精彩活动取消报名")
    public Result cancelRegistration(@PathVariable @NotNull Long cancelActivityId, HttpServletRequest request) {
        log.info("取消活动id信息:{}", cancelActivityId);
        String token = request.getHeader("Authorization");
        if(token==null) return Result.error("用户未登录");
        goodregistrationService.cancelRegistration(token,cancelActivityId);
        goodactivityService.changeSubCount(cancelActivityId,-1);
        return Result.success("取消成功");
    }

}
