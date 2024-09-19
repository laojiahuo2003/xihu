package com.demo.xihu.controller;

import com.demo.xihu.dto.GoodregistrationDTO;
import com.demo.xihu.dto.RegistrationDTO;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.*;
import com.demo.xihu.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/goodregistration")
@Slf4j
@Tag(name = "用户_精彩活动相关接口", description = "报名")
public class GoodregistrationController {
    @Autowired
    private GoodregistrationService goodregistrationService;
    @Autowired
    private GoodactivityService goodactivityService;
    @Autowired
    private RedisService redisService;


    @PostMapping()
    @Operation(summary = "精彩活动报名")
    public Result registerForActivity(@RequestBody GoodregistrationDTO goodregistrationDTO) {
        log.info("活动信息:{}",goodregistrationDTO);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer)claims.get("id");
        goodregistrationService.register(id,goodregistrationDTO);
        goodactivityService.changeSubCount(goodregistrationDTO.getActivityId(),1);
        return Result.success("报名成功");
    }

    @DeleteMapping("/{cancelActivityId}")
    @Operation(summary = "精彩活动取消报名")
    public Result cancelRegistration(@PathVariable @NotNull Long cancelActivityId) {
        log.info("取消活动id信息:{}", cancelActivityId);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer)claims.get("id");
        goodregistrationService.cancelRegistration(id,cancelActivityId);
        goodactivityService.changeSubCount(cancelActivityId,-1);
        return Result.success("取消成功");
    }

}
