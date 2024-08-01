package com.demo.xihu.interceptors;

import com.demo.xihu.exception.UnauthorizedAccessException;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.result.Result;
import com.demo.xihu.service.RedisService;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String token = request.getHeader("Authorization");
        try {
            //从redis获取相同的token
            String redisToken = redisService.get(token);
            if(redisToken==null){
                //token已经失效或不存在
                throw new UserNotLoginException("用户token失效");
            }
            //解析token
            Map<String, Object> claims = JwtUtil.parseToken(token);
            //解析成功--->>把业务数据储存到当前线程中
            ThreadLocalUtil.set(claims);
            //放行
            return true;
        }catch (Exception e) {
            response.setStatus(200);
            throw new UserNotLoginException("用户未登录");
            //return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的数据
        ThreadLocalUtil.remove();
    }
}
