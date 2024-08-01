package com.demo.xihu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.config.RedisTemplateConfig;
import com.demo.xihu.dto.QueryActivitiesDTO;
import com.demo.xihu.dto.QueryGoodactivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.Goodactivity;
import com.demo.xihu.mapper.GoodactivityMapper;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.GoodactivityService;
import com.demo.xihu.service.GoodregistrationService;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.vo.ActivityListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private RedisService redisService;

    @GetMapping("/goodactivities/Info")
    @Operation(summary = "搜索订阅的活动")
    public Result getActivityByToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        log.info("getActivityByToken：{}",token);
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            Integer userid = (Integer) claims.get("id");
            log.info("解析出来的id：{}",userid);
            //这里利用redis减轻数据库压力
            List<Goodactivity> activityList;
            if(redisService.get("goodactlist_"+userid)!=null){
                System.out.println("从缓存里拿到了"+redisService.get("actlist_" + userid));
                activityList =(List<Goodactivity>)JSON.parse( redisService.get("actlist_" + userid) );
                //activityList=goodactivityService.listById(userid);
            }else{
                activityList=goodactivityService.listById(userid);
                redisService.set("goodactlist_"+userid,JSON.toJSONString( activityList));
                System.out.println("放入redis:"+JSON.toJSONString( activityList));
            }

            return Result.success("token有效",activityList);
        }catch (Exception e) {
            return Result.error("token无效");
        }
    }


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
