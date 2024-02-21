package com.demo.xihu.controller;

import com.demo.xihu.result.Result;

import com.demo.xihu.utils.CommonUtil;
import com.google.code.kaptcha.Producer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequestMapping("/dev-api/captcha")
@RestController
public class CaptchaController {
    @Autowired
    private Producer captchaProducer;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/get")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response){
        String text = captchaProducer.createText();
        //存入redis
        redisTemplate.opsForValue().set(getCaptchaKey(request),text,30, TimeUnit.SECONDS);
        //生成图片
        BufferedImage image = captchaProducer.createImage(text);
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("check")
    public Result checkCaptcha(String captcha, HttpServletRequest request){
        Object value = redisTemplate.opsForValue().get(getCaptchaKey(request));
        if(value==null){
            return Result.error("图形验证码过期");
        }
        log.info("图形验证码："+value+"传入验证码"+captcha);
        if(captcha.equals(value)){
            //登录成功后 删除redis的验证码缓存
            redisTemplate.delete(getCaptchaKey(request));
            return Result.success("图形验证码正确");
        }
        return Result.error("图形验证码错误");
    }
    //获取key
    private String getCaptchaKey(HttpServletRequest request){
        String ip = CommonUtil.getIpAddr(request);//获取ip
        String userInfo = request.getHeader("User-Agent");//获取请求头中的User-Agent
        String md5 = CommonUtil.MD5(ip + userInfo);//生成md5
        return "captcha:"+md5;
    }
}
