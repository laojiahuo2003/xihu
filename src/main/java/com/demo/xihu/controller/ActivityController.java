package com.demo.xihu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.demo.xihu.dto.DateActivitiesVO;
import com.demo.xihu.dto.QueryActivitiesDTO;
import com.demo.xihu.entity.Activity;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.RegistrationService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.vo.ActivityListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("dev-api")
@Slf4j
@Tag(name = "活动(大会)相关接口", description = "这是描述")
public class ActivityController {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ActivityService activityService;

    @GetMapping("/activities/Info")
    @Operation(summary = "搜索订阅的活动")
    public Result getActivityByToken(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        log.info("getActivityByToken：{}",token);
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            Integer userid = (Integer) claims.get("id");
            log.info("解析出来的id：{}",userid);
            List<Activity> activityList=activityService.listById(userid);
            return Result.success("token有效",activityList);
        }catch (Exception e) {
            return Result.error("token无效");
        }
    }



    @PostMapping("/activities/list")
    @Operation(summary = "根据条件查询活动")
    public Result queryActivities(@RequestBody QueryActivitiesDTO queryActivitiesDTO, HttpServletRequest request) {
        Integer num = queryActivitiesDTO.getNum();
        if(num==null||num<=-1){
            queryActivitiesDTO.setNum(null);
        }
        System.out.println(queryActivitiesDTO);
        //根据条件选择
        List<ActivityListVO> activityListVO = activityService.listByParams(queryActivitiesDTO);
        String token = request.getHeader("Authorization");
        //尝试从token获取id
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            Integer userId = (Integer) claims.get("id");
            List<Integer> activityIds = registrationService.findSubbyUserId(userId);
            for (ActivityListVO vo : activityListVO) {
                Long voId = vo.getId(); // 获取当前 ActivityListVO 对象的 ID
                // 将 activityIds 中的 Integer 转换为 Long，并进行比较
                if (activityIds.stream().map(Integer::longValue).collect(Collectors.toList()).contains(voId)) {
                    vo.setIsSub(1);  // 假设 setIsSub 是用来设置 isSub 属性的方法
                }
            }
        }catch (Exception e){

        }

        return Result.success("查找成功",activityListVO);
    }

    /**
     * 模糊查询活动名
     * @param title
     * @return
     */
    @GetMapping("/activities/search")
    @Operation(summary = "模糊查询活动title")
    public Result searchActivities(@RequestParam String title) {
        log.info("活动名称:{}",title);
        List<Activity> activities = activityService.searchByTitle(title);
        return Result.success("查询成功",activities);
    }

    /**
     * 组合条件查询活动
     * @param startDate
     * @param endDate
     * @param location
     * @return
     */
    @GetMapping("/activities/filter")
    @Operation(summary = "组合条件查询活动")
    public Result filterActivities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String location) {
        log.info("开始组合条件查询活动: 开始日期={}, 结束日期={}, 地点={}", startDate, endDate, location);
        List<Activity> activities = activityService.filterActivities(startDate, endDate, location);
        return  Result.success("查询成功",activities);
    }


    /**
     * 添加活动
     * @param activity
     * @return
     */
    @PostMapping("/admin/activities")
    @Operation(summary = "管理员增加活动")
    public Result createActivity(@RequestBody  Activity activity) {
        log.info("添加的活动:{}",activity);
        if(activity.getTitle()==null) return Result.error("活动名不能为空");
        activityService.createActivity(activity);
        return Result.success("添加成功");
    }


    /**
     * 分页查询所有活动
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/activities/pagelist")
    @Operation(summary = "分页查询所有活动")
    public Result<Page<Activity>> getActivitiesByPage(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("pageno:{},pagesize:{}",pageNo,pageSize);
        Page<Activity> page = activityService.getActivitiesByPage(pageNo, pageSize);
        return Result.success("查询成功",page);
    }

    /**
     * 根据id更新活动信息
     * @param activity
     * @return
     */
    @PutMapping("/admin/activities")
    @Operation(summary = "根据id更新活动信息")
    public Result updateActivity(@RequestBody Activity activity) {
        log.info("活动id:{}",activity.getId());
        if(activity.getId()==null) return Result.error("活动id缺失");
        activityService.updateActivity(activity);
        return Result.success("更新成功");
    }


    /**
     * 根据id删除活动信息
     * @param id
     * @return
     */
    @DeleteMapping("/admin/activities/{id}")
    @Operation(summary = "根据id删除活动信息")
    public Result deleteActivity(@PathVariable Long id) {
        log.info("活动id:{}",id);
        if(activityService.getById(id)==null) return Result.error("活动不存在");
        activityService.deleteActivity(id);
        return Result.success("删除成功");
    }
}