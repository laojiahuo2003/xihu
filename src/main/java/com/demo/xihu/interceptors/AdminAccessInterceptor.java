package com.demo.xihu.interceptors;

import com.demo.xihu.exception.UnauthorizedAccessException;
import com.demo.xihu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

@Component
public class AdminAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 判断管理员权限
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims != null) {
                Integer userType = (Integer) claims.get("userType");
                if (userType != null && userType != 0) {
                    throw new UnauthorizedAccessException("无管理员权限");
                }
                return true;
            }
        } catch (Exception e) {
            response.setStatus(401);
            throw new UnauthorizedAccessException("无管理员权限");//如果要返回给前端信息可以抛出异常，这里已经被捕获了，全局异常处理器不处理该异常
//            response.setStatus(401);
//            return false;
        }
        return true;
    }
}
