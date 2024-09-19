package com.demo.xihu.interceptors;

import com.alibaba.fastjson.JSONObject;
import com.demo.xihu.constant.RedisConstant;
import com.demo.xihu.utils.JwtUtil;
import com.demo.xihu.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StringUtils.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        String key  = RedisConstant.LOGIN_USER_KEY + token;
        String userJsonString = stringRedisTemplate.opsForValue().get(key);
        // 3.判断用户是否存在
        if (userJsonString == null|| userJsonString.isEmpty()) {
            return true;
        }
        // 5.转换成用户对象
        Map<String, Object> claims = JSONObject.parseObject(userJsonString);
        ThreadLocalUtil.set(claims);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, RedisConstant.LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        ThreadLocalUtil.remove();
    }
}
	