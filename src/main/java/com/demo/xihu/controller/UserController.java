package com.demo.xihu.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.dto.LoginbyAccountDTO;
import com.demo.xihu.entity.Point;
import com.demo.xihu.entity.User;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.PointService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.service.UserpointService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.InfoUserVO;
import com.demo.xihu.vo.PointsRecordVO;
import com.demo.xihu.vo.PointsStatusVO;
import com.demo.xihu.vo.loginUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dev-api/user")
@Slf4j
@Tag(name = "用户相关接口", description = "这是描述")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaController captchaController;

    @Autowired
    private PointService pointService;
    @Autowired
    private UserpointService userpointService;


    /**
     * 用户积分明细
     * @return
     */
    @PostMapping("/pointsItem")
    public Result getPointsItem(){
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        List<PointsRecordVO> pointsRecordVOS = userpointService.pointsRecordList(userId);
        return Result.success(pointsRecordVOS);
    }


    /**
     * 用户积分任务完成情况
     * @return
     */
    @PostMapping("/pointsStatus")
    public Result getPointStatus(){
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        List<PointsStatusVO> pointsStatusVO = userpointService.getPointsStatus(userId);
        return Result.success(pointsStatusVO);
    }


    /*
    {
        pointname:"积分活动"
    }
    */
    @PostMapping("/getpoints")
    public Result getPoints(@RequestBody Map<String, String> params){
        log.info("传入的参数{}",params);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("id");
        //查出point的数据
        Point point=pointService.getbyName(params.get("pointname"));
        Long pointId = point.getId();
        Integer pointnum = point.getPointnum();

        //在userpoint表中操作，1未找到就增加。2找到后判断时间是否符合，符合加，不符合返回今日已签到
        Result result = userpointService.addressPoint(userId,pointId,pointnum);//传入用户id，活动id以及活动积分
        Integer code = result.getCode();
        if(code.equals(0)){
            //积分增加操作
            userService.addpoint(userId,pointnum);
        }
        return result;
    }
    /**
     * 注销接口
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        //userService.logout(token);
        return Result.success("注销成功");
    }

//    /**
//     * 请求头传入token，实现查询登陆的用户个人信息
//     *
//     * @return
//     */
//    @GetMapping("/userInfo")
//    @Operation(summary = "查询登陆的用户个人信息(废弃，还没删)")
//    public Result<User> userInfo(/*@RequestHeader(name="Authorization")String token*/) {
//        //获取该线程的值
//        Map<String, Object> claims = ThreadLocalUtil.get();
//        String account = (String) claims.get("account");
//        User user = userService.findByAccount(account);
//        return Result.success("查询成功",user);
//    }

    /**
     * 判断token情况
     * @param request
     * @return
     */
    @GetMapping("/Info")
    @Operation(summary = "判断token情况")
    public Result tokenReader(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        log.info("tokenReader：{}",token);
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            Integer id = (Integer) claims.get("id");
            log.info("解析出来的id：{}",id);
//            User user = userService.getById(id);
            InfoUserVO infoUserVO = userService.getById(id);
//            InfoUserVO infoUserVO = new InfoUserVO();
//            BeanUtils.copyProperties(user,infoUserVO);
            return Result.success("token有效",infoUserVO);
        }catch (Exception e) {
            return Result.error("token无效");
        }
    }


    @PatchMapping("/updatePwd")
    @Operation(summary = "更新用户密码")
    public Result updatePwd(@RequestBody Map<String, String> params) {
        log.info("更新用户密码:{}",params);
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        //校验参数
        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,15}$";
        if (oldPwd == null || newPwd == null || rePwd == null) {
            //参数值为空，抛出异常或返回错误提示信息
            return Result.error("参数值不能为空");
        } else if (!newPwd.equals(rePwd)) {
            //新密码和确认密码不匹配，抛出异常或返回错误提示信息
            return Result.error("新密码和确认密码不匹配");
        } else if (oldPwd.equals(newPwd)) {
            //新密码和旧密码相同，抛出异常或返回错误提示信息
            return Result.error("新密码不能与旧密码相同");
        } else if (!newPwd.matches(pattern)) {
            // 新密码长度不符合要求，抛出异常或返回错误提示信息
            return Result.error("密码不符合要求：必须为8-15位，且包含数字、大小写字母");
        } else {
            //参数校验通过,处理修改密码的业务逻辑
            Map<String, Object> claims = ThreadLocalUtil.get();//获取当前线程数据
            String account = (String) claims.get("account");//解析出token中的username
            User loginUser = userService.findByAccount(account);//找到当前登录用户数据
            if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))) {
                return Result.error("原密码不正确");
            }
            //进行密码更新
            userService.updatePwd(newPwd);
            return Result.success("密码修改成功");
        }
    }

    /**
     * 根据token更新用户头像
     *
     * @param avatarUrl
     * @return
     */
    @PatchMapping("/updateAvatar")
    @Operation(summary = "更新用户头像")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success("更新成功");
    }

    /**
     *修改个人信息
     * @param userDTO
     * @return
     */
    @PostMapping("/updateInfo")
    @Operation(summary = "修改个人信息")
    public Result userUpdate(@RequestBody User userDTO){
        log.info("修改个人信息传入DTO",userDTO);
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer id = (Integer) claims.get("id");
        userDTO.setId((long)id);
        if(userService.exists(new QueryWrapper<User>().ne("id",userDTO.getId()).eq("username",userDTO.getUsername()))) return Result.error("用户名已存在");
        if(userService.updateById(userDTO)) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    /**
     * 账号登录接口
     *
     * @param loginbyAccountDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "账号登录")
    public Result login(@RequestBody LoginbyAccountDTO loginbyAccountDTO, HttpServletRequest request) {
        log.info("账号登陆DTO",loginbyAccountDTO);

        String captcha = loginbyAccountDTO.getCaptcha();
        String account = loginbyAccountDTO.getAccount();
        String password = loginbyAccountDTO.getPassword();
        //检验验证码
        Result result = captchaController.checkCaptcha(captcha, request);
        if (result.getCode() == 1) {
            return result;
        }
        // 账号合法性校验
        if (account == null) {
            return Result.error("账号不能为空", 1);
        }
//        String pattern = "^[a-zA-Z0-9]{5,10}$";
//        if (!account.matches(pattern)) {
//            return Result.error("账号不符合要求：必须为5-10位，且只能包含英文字母和数字", 1);
//        }
        // 参数合法性校验
        if (password == null) {
            return Result.error("密码不能为空", 1);
        }
//        String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,15}$";
//        if (!password.matches(pattern)) {
//            return Result.error("密码不符合要求：必须为8-15位，且包含数字、大小写字母", 1);
//        }
        //判断是否存在该用户
        User loginUser = userService.findByAccount(account);
        if (loginUser == null) return Result.error("账号不存在");
        //判断密码是否正确
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("account", loginUser.getAccount());
            claims.put("userType", loginUser.getUserType());
            String token = JwtUtil.genToken(claims);
            loginUserVO loginUserVO=new loginUserVO();
            BeanUtils.copyProperties(loginUser,loginUserVO);
            loginUserVO.setToken(token);
            return Result.success("登陆成功",loginUserVO);
        }
        return Result.error("密码错误");
    }






}