package com.example.demo.interceptors;

import com.example.demo.util.JWTUtil;
import com.example.demo.util.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 令牌验证
        String token = request.getHeader("Authorization");
        // 检查token是否为空
        if (token == null || token.isEmpty()) {
            System.out.println("token为空");
            response.setStatus(401);
            return false;
        }
        // 验证令牌是否正确
        try {
            // 从redis中获取相同的token
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            String redisToken = opsForValue.get(token);
            if (redisToken == null) {
                System.out.println("redis为空");

                throw new RuntimeException("Token not found in Redis");
            }
            System.out.println(redisToken);
            Claims claims = jwtUtil.validateToken(token);
            final String extractedUsername = claims.getSubject();
            if (extractedUsername != null && !jwtUtil.isTokenExpired(token)) {
                // 将数据保存到ThreadLocal
                ThreadLocalUtil.set(claims);
                // 放行
                return true;
            } else {
                throw new RuntimeException("Token is invalid or expired");
            }
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            response.setStatus(401);
            // 不放行
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 清除线程变量
        ThreadLocalUtil.remove();
    }
}