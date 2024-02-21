package com.demo.xihu.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.dto.LoginByPhoneDTO;
import com.demo.xihu.dto.PhoneDTO;
import com.demo.xihu.dto.RegisterDTO;
import com.demo.xihu.entity.User;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.CodeUtil;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.SmsTool;
import com.demo.xihu.vo.loginUserVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dev-api/sms")
@Slf4j
public class SmsCtrlController {

    @Autowired
    private RedisService redisService;
    private String tokenId = "TOKEN-USER-";
    @Autowired
    private CaptchaController captchaController;
    @Autowired
    private UserService userService;

    /**
     * 发送短信
     *
     * @throws ClientException 抛出异常
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     */
    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result smsXxs(@RequestBody PhoneDTO phoneDTO) throws ClientException {
        log.info("手机号:{}", phoneDTO);
        String phone = phoneDTO.getPhone();
        // 调用工具类中生成验证码方法（指定长度的随机数）
        String code = CodeUtil.generateVerifyCode(6);
        // 填充验证码
        String TemplateParam = "{\"code\":\"" + code + "\"}";
        System.out.println("phone" + phone + "验证码：" + code);
        // 传入手机号码及短信模板中的验证码占位符
        //SendSmsResponse response = SmsTool.sendSms(phone, TemplateParam);
        if (true/*response.getCode().equals("OK")*/) {
            // 验证码绑定手机号并存储到redis
            redisService.set(tokenId + phone, code);
            redisService.expire(tokenId + phone, 620); // 调用redis工具类中存储方法设置超时时间
            return Result.success("发送成功");
        }
        return Result.error("发送失败");
    }

    /**
     * 手机号登陆
     *
     * @throws ClientException 抛出异常
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     */
    @ResponseBody
    @RequestMapping(value = "/loginbyphone", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result validateNum(@RequestBody LoginByPhoneDTO loginByPhoneDTO, HttpServletRequest request) throws ClientException {
        log.info("手机登录信息:{}", loginByPhoneDTO);
        String phone = loginByPhoneDTO.getPhone();
        if (phone == null || phone.length() != 11) return Result.error("手机号不合法");
        String verifyCode = loginByPhoneDTO.getVerifyCode();
        String captcha = loginByPhoneDTO.getCaptcha();

        //检验图形验证码
        Result result = captchaController.checkCaptcha(captcha, request);
        if (result.getCode() == 1) {
            return result;
        }
        // 短信验证码
        /*String redisauthcode = redisService.get(tokenId + phone); // 传入tokenId返回redis中的value
        if (StringUtils.isEmpty(redisauthcode)) {
            // 如果未取到则过期验证码已失效
            return Result.error("短信验证码失效");
        } else if (!verifyCode.equals(redisauthcode)) {
            // 验证码错误
            return Result.error("短信验证码错误");
        }*/
        // 返回用户
        User loginUser = userService.getOne(new QueryWrapper<User>().eq("phone", phone));
        if (loginUser == null) return Result.error("手机号不存在");
        //准备返回数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", loginUser.getId());
        claims.put("account", loginUser.getAccount());
        claims.put("userType", loginUser.getUserType());
        String token = JwtUtil.genToken(claims);

        //准备返回用户数据
        loginUserVO loginUserVO = new loginUserVO();
        BeanUtils.copyProperties(loginUser, loginUserVO);
        loginUserVO.setToken(token);
        return Result.success("登陆成功", loginUserVO);

    }

    /**
     * 注册接口
     * @param registerDTO
     * @param request
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public Result register(@RequestBody RegisterDTO registerDTO, HttpServletRequest request) {

        log.info("注册用户信息{}",registerDTO);
        //获取数据
        String account = registerDTO.getAccount();
        String phone = registerDTO.getPhone();
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();
        String verifyCode = registerDTO.getVerifyCode();
        String captcha = registerDTO.getCaptcha();

        if (account == null  || phone == null || phone.length() != 11 || username == null || username.length() > 16 || password == null)
            return Result.error("参数不合法", 1);

        String  pattern = "^[a-zA-Z0-9]{5,10}$";
        if (!account.matches(pattern)) {
            return Result.error("账号不符合要求：必须为5-10位，且只能包含英文字母和数字", 1);
        }
        //暂时不需要
//        pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,15}$";
//        if (!password.matches(pattern)) {
//            return Result.error("密码不符合要求：必须为8-15位，且包含数字、大小写字母", 1);
//        }

        //图形验证码
        Result result = captchaController.checkCaptcha(captcha, request);
        if (result.getCode() == 1) {
            return result;
        }
        // 短信验证码
        /*String redisauthcode = redisService.get(tokenId + phone); // 传入tokenId返回redis中的value
        if (StringUtils.isEmpty(redisauthcode)) {
            // 如果未取到则过期验证码已失效
            return Result.error("短信验证码失效");
        } else if (!verifyCode.equals(redisauthcode)) {
            // 验证码错误
            return Result.error("短信验证码错误");
        }*/

        QueryWrapper<User> queryWrapperAccount = new QueryWrapper<>();
        queryWrapperAccount.eq("account", account);
        if (userService.exists(queryWrapperAccount)) {
            return Result.error("账号已被占用");
        }
        QueryWrapper<User> queryWrapperPhone = new QueryWrapper<>();
        queryWrapperPhone.eq("phone", phone);
        if (userService.exists(queryWrapperPhone)) {
            return Result.error("手机号已被占用");
        }
        QueryWrapper<User> queryWrapperUsername = new QueryWrapper<>();
        queryWrapperUsername.eq("username", username);
        if (userService.exists(queryWrapperUsername)) {
            return Result.error("用户名已被占用");
        }
        userService.register(account, password, phone, username);
        return Result.success("注册成功");

    }

}

//    /**
//     * 发送短信
//     * @ResponseBody 返回json数据
//     * @RequestMapping 拦截请求，指定请求类型：POST
//     * @RequestBody 接受前台传入的json数据 接受类型为Map
//     * @throws ClientException 抛出异常
//     */
//    @ResponseBody
//    @RequestMapping(value = "/smsXxs", method = RequestMethod.POST, headers = "Accept=application/json")
//    public Map<String,Object> smsXxs(@RequestBody Map<String,Object> requestMap,HttpServletRequest request) throws ClientException {
//        Map<String,Object> map = new HashMap<>();
//        String phone = requestMap.get("phoneNumber").toString();
//        // 调用工具栏中生成验证码方法（指定长度的随机数）
//        String code = CodeUtil.generateVerifyCode(6);
//        //填充验证码
//        String TemplateParam = "{\"code\":\""+code+"\"}";
//        SendSmsResponse response = SmsTool.sendSms(phone,TemplateParam);//传入手机号码及短信模板中的验证码占位符
//
//        map.put("verifyCode",code);
//        map.put("phone",phone);
//        request.getSession().setAttribute("CodePhone",map);
//        if( response.getCode().equals("OK")) {
//            map.put("isOk","OK");
//            //验证码绑定手机号并存储到redis
//            redisService.set(tokenId+phone,code);
//            redisService.expire(tokenId+phone,620);//调用reids工具类中存储方法设置超时时间
//        }
//        return map;
//    }
//
//
//    /**
//     * 注册验证
//     * @ResponseBody 返回json数据
//     * @RequestMapping 拦截请求，指定请求类型：POST
//     * @RequestBody 接受前台传入的json数据 接受类型为Map
//     * @throws ClientException 抛出异常
//     */
//    @ResponseBody
//    @RequestMapping(value = "/validateNum", method = RequestMethod.POST, headers = "Accept=application/json")
//    public Map<String, Object> validateNum(@RequestBody Map<String,Object> requestMap) throws ClientException {
//
//        Map<String, Object> map = new HashMap<>();
//        String phone = requestMap.get("phone").toString();//获取注册手机号码
//        String verifyCode = requestMap.get("verifyCode").toString();//获取手机验证码
//
//        //首先比对验证码是否失效
//        String redisauthcode = redisService.get(tokenId + phone); //传入tonkenId返回redis中的value
//        if (StringUtils.isEmpty(redisauthcode)) {
//            //如果未取到则过期验证码已失效
//            map.put("ruselt", 404);
//        } else if (!"".equals(redisauthcode) && !verifyCode.equals(redisauthcode)) {
//            //验证码错误
//            map.put("ruselt", 500);
//        } else {
//            //用户注册成功
//            map.put("ruselt", 200);
//        }
//        return map;
//    }