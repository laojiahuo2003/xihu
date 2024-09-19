package com.demo.xihu.controller;

import com.demo.xihu.entity.Activity;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.UnauthorizedAccessException;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.ActivityService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.ThreadLocalUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/admin")
@Slf4j
@Tag(name = "管理员相关接口", description = "这是描述")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private ActivityService activityService;
    /**
     * 根据id查询信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询信息")
    public Result<User> getById(@PathVariable Long id){
        //判断管理员权限
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userType = (Integer) claims.get("userType");
        if (userType!=0) {
            return Result.error("无管理员权限");/*throw new UnauthorizedAccessException("无管理员权限");*/
        }

        //调用service层
        User user=userService.getById(id);
        log.info("查询用户:{}",user);
        if(user==null) return Result.error("查询失败");
        else return Result.success("查询成功",user);
    }

    @PutMapping("/updateById")
    @Operation(summary = "根据Id更新用户信息")
    public Result updateById(@RequestBody @Validated User userDTO){
        log.info("更新用户信息:{}",userDTO);
        User user = userService.getById(userDTO.getId());
        if(user==null) return Result.error("该用户id不存在",1);
        userService.myUpdateById(userDTO);
        return Result.success("更新成功");
    }

    /**
     * 根据账号更新用户信息
     * @param userDTO
     * @return
     */
    @PutMapping("/updateByUsername")
    @Operation(summary = "根据用户名更新用户信息")
    public Result updateByAccount(@RequestBody User userDTO){
        log.info("更新用户信息:{}",userDTO);
        User user = userService.findByAccount(userDTO.getAccount());
        if(user==null) return Result.error("该用户名不存在",1);
        userService.updateByAccount(userDTO);
        return Result.success("更新成功");
    }

    /**
     * 新增用户
     * @param user
     * @return
     */
    @PostMapping
    @Operation(summary = "新增用户")
    public Result<String> save(@RequestBody User user){
        log.info("新增用户信息:{}",user);
        //查询用户
        User u = userService.findByAccount(user.getAccount());
        if(u!=null){return Result.error("账号已被占用",2);}
        log.info("新增用户:{}",user);
        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        userService.save(user);
        return Result.success("添加成功");
    }


    /**
     * 添加活动
     * @param activity
     * @return
     */
    @PostMapping("/activities")
    @Operation(summary = "管理员增加活动")
    public Result createActivity(@RequestBody Activity activity) {
        log.info("添加的活动:{}",activity);
        if(activity.getTitle()==null) return Result.error("活动名不能为空");
        activityService.createActivity(activity);
        return Result.success("添加成功");
    }
    /**
     * 根据id更新活动信息
     * @param activity
     * @return
     */
    @PutMapping("/activities")
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
    @DeleteMapping("/activities/{id}")
    @Operation(summary = "根据id删除活动信息")
    public Result deleteActivity(@PathVariable Long id) {
        log.info("活动id:{}",id);
        if(activityService.getById(id)==null) return Result.error("活动不存在");
        activityService.deleteActivity(id);
        return Result.success("删除成功");
    }
}
