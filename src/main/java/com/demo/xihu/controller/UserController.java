package com.demo.xihu.controller;


import com.demo.xihu.dto.LoginbyAccountDTO;
import com.demo.xihu.entity.User;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.ThreadLocalUtil;
import com.demo.xihu.vo.loginUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
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
     * 请求头传入token，实现查询登陆的用户个人信息
     *
     * @return
     */
    @GetMapping("/userInfo")
    @Operation(summary = "查询登陆的用户个人信息")
    public Result<User> userInfo(/*@RequestHeader(name="Authorization")String token*/) {
        //获取该线程的值
        Map<String, Object> claims = ThreadLocalUtil.get();
        String account = (String) claims.get("account");
        User user = userService.findByAccount(account);
        return Result.success("查询成功",user);
    }

//    /**
//     * 注册接口
//     *
//     * @param account
//     * @param password
//     * @return
//     */
//    @PostMapping("/register")
//    @Operation(summary = "注册")
//    public Result register(String account, String phone, String username, String password) {
//
//        if (account == null || account.length() < 2 || account.length() > 16 ||
//                phone == null || phone.length() != 11 ||
//                username == null || username.length() > 16 ||
//                password == null || password.length() < 2 || password.length() > 16)
//            return Result.error("参数不合法", 1);
//
//        QueryWrapper<User> queryWrapperAccount = new QueryWrapper<>();
//        queryWrapperAccount.eq("account", account);
//        if (userService.exists(queryWrapperAccount)) {
//            return Result.error("账号已被占用");
//        }
//        QueryWrapper<User> queryWrapperPhone = new QueryWrapper<>();
//        queryWrapperPhone.eq("phone", phone);
//        if (userService.exists(queryWrapperPhone)) {
//            return Result.error("手机号已被占用");
//        }
//        QueryWrapper<User> queryWrapperUsername = new QueryWrapper<>();
//        queryWrapperUsername.eq("username", username);
//        if (userService.exists(queryWrapperUsername)) {
//            return Result.error("用户名已被占用");
//        }
//        userService.register(account, password, phone, username);
//        return Result.success("注册成功");
//
//    }

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
        String pattern = "^[a-zA-Z0-9]{5,10}$";
        if (!account.matches(pattern)) {
            return Result.error("账号不符合要求：必须为5-10位，且只能包含英文字母和数字", 1);
        }
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
        if (loginUser == null) return Result.error("用户不存在");
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
    //    /**
//     * 根据id查询信息
//     * @param id
//     * @return
//     */
//    @GetMapping("/{id}")
//    @ApiOperation("根据id查询信息")
//    public Result<User> getById(@PathVariable Long id){
//        User user=userService.getById(id);
//        log.info("查询用户:{}",user);
//        if(user==null) return Result.error("查询失败");
//        else return Result.success(user);
//    }
//
//    /**
//     * 根据用户名更新用户信息
//     * @param userDTO
//     * @return
//     */
//    @PutMapping("/updateByUsername")
//    @ApiOperation("根据用户名更新用户信息")
//    public Result updateByUsername(@RequestBody User userDTO){
//        log.info("更新用户信息:{}",userDTO);
//        User user = userService.findByUsername(userDTO.getUsername());
//        if(user==null) return Result.error("该用户名不存在",1);
//        userService.updateByUsername(userDTO);
//        return Result.success();
//    }
//
//    /**
//     * 新增用户
//     * @param user
//     * @return
//     */
//    @PostMapping
//    @ApiOperation("新增用户")
//    public Result<String> save(@RequestBody User user){
//        //查询用户
//        User u = userService.findByUsername(user.getUsername());
//        if(u!=null){return Result.error("用户名已被占用",2);}
//        log.info("新增用户:{}",user);
//        user.setPassword(Md5Util.getMD5String(user.getPassword()));
//        userService.save(user);
//        return Result.success();
//    }

}