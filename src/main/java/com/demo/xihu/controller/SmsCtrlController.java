package com.demo.xihu.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.demo.xihu.entity.User;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.service.UserService;
import com.demo.xihu.utils.CodeUtil;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.Md5Util;
import com.demo.xihu.utils.SmsTool;
import com.demo.xihu.vo.UserLoginVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sms")
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
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     * @throws ClientException 抛出异常
     */
    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result smsXxs(@RequestBody Map<String, Object> requestMap) throws ClientException {
        String phone = requestMap.get("phone").toString();
        // 调用工具类中生成验证码方法（指定长度的随机数）
        String code = CodeUtil.generateVerifyCode(6);
        // 填充验证码
        String TemplateParam = "{\"code\":\"" + code + "\"}";
        System.out.println("phone"+phone+"验证码："+code);
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
     * 注册验证
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     * @throws ClientException 抛出异常
     */
    @ResponseBody
    @RequestMapping(value = "/loginbyphone", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result validateNum(String phone, String verifyCode, String captcha, HttpServletRequest request) throws ClientException {

        //检验图形验证码
        Result result = captchaController.checkCaptcha(captcha, request);
        if (result.getCode() == 1) {
            return result;
        }
        // 首先比对验证码是否失效
        String redisauthcode = redisService.get(tokenId + phone); // 传入tokenId返回redis中的value
        if (StringUtils.isEmpty(redisauthcode)) {
            // 如果未取到则过期验证码已失效
            return Result.error("短信验证码失效");
        } else if (!verifyCode.equals(redisauthcode)) {
            // 验证码错误
            return Result.error("短信验证码错误");
        } else {
            // 返回用户
            User loginUser = userService.getOne(new QueryWrapper<User>().eq("phone", phone));
            if (loginUser == null) return Result.error("用户不存在");
            //准备返回数据
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("account", loginUser.getAccount());
            claims.put("userType", loginUser.getUserType());
            String token = JwtUtil.genToken(claims);
            UserLoginVO userLoginVO = UserLoginVO.builder()
                    .id(loginUser.getId())
                    .account(loginUser.getAccount())
                    .token(token)
                    .build();
            return Result.success("登录成功",userLoginVO);
        }
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