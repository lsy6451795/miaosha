package com.syliu.miaosha.RateLimit;

import com.syliu.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Component
public class UserRateLimiterIntercept implements HandlerInterceptor {
    @Autowired
    private  RedisLimiterUtils redisLimiterUtils;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod=(HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            /**
             * 首先获取方法上的注解
             */
            UserRateLimit rateLimit= AnnotationUtils.findAnnotation(method, UserRateLimit.class);
            //方法上没有标注该注解，尝试获取类上的注解
            if(Objects.isNull(rateLimit)){
                //获取类上的注解
                rateLimit = AnnotationUtils.findAnnotation(handlerMethod.getBean().getClass(), UserRateLimit.class);

            }
            if(Objects.isNull(rateLimit)){
                return true;
            }

            if (!redisLimiterUtils.tryAcquire(request.getRequestURI()+getCookieValue(request, MiaoshaUserService.Cookie_Name_token), rateLimit.capacity(), rateLimit.rate())) {
                //抛出请求超时的异常
                throw new TimeoutException();
            }

        }
        return  true;



    }
    private String getCookieValue(HttpServletRequest request, String cookie_name_token) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<=0)return  null;
        for (Cookie cookie:cookies
        ) {
            if(cookie.getName().equals(cookie_name_token))return cookie.getValue();

        }
        return null;
    }
}
