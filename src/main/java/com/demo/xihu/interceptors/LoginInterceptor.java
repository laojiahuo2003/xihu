package com.demo.xihu.interceptors;

import com.demo.xihu.exception.UnauthorizedAccessException;
import com.demo.xihu.exception.UserNotLoginException;
import com.demo.xihu.result.Result;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String token = request.getHeader("Authorization");
        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            //把业务数据储存到当前线程中
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
