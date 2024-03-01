package com.demo.xihu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.dto.QueryActivitiesDTO;
import com.demo.xihu.dto.QueryGoodactivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Goodactivity;
import com.demo.xihu.mapper.GoodactivityMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.GoodactivityService;
import com.demo.xihu.service.GoodregistrationService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.vo.ActivityListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("dev-api")
@Slf4j
@Tag(name = "精彩活动相关接口", description = "这是描述")
public class GoodactivityController {

    @Autowired
    private GoodactivityService goodactivityService;
    @Autowired
    private GoodregistrationService goodregistrationService;

    @PostMapping("/goodactivities/list")
    @Operation(summary = "根据条件查询活动")
    public Result queryGoodactivities(@RequestBody QueryGoodactivitiesDTO queryGoodactivitiesDTO, HttpServletRequest request){
        Page<Goodactivity> page = goodactivityService.getActivitiesByPage(queryGoodactivitiesDTO);
        //尝试获取id
        String token = request.getHeader("Authorization");
        //尝试从token获取id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("id");
            List<Integer> goodactivityIdsInt = goodregistrationService.findSubbyUserId(userId);
            List<Long> goodactivityIds = goodactivityIdsInt.stream()//转化为Long，才能比较
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            //处理isSub
            page.getRecords().forEach(goodactivity -> {
                goodactivity.setIsSub(goodactivityIds.contains(goodactivity.getId()) ? 1 : 0);
            });
        }catch (Exception e){
        }
        //重构page，添加isSub
        return Result.success("查询成功",page);
    }
}
