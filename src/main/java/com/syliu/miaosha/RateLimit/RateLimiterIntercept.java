package com.syliu.miaosha.RateLimit;

import com.syliu.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Component
public class RateLimiterIntercept implements HandlerInterceptor {
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
           RateLimit rateLimit= AnnotationUtils.findAnnotation(method,RateLimit.class);
           //方法上没有标注该注解，尝试获取类上的注解
           if(Objects.isNull(rateLimit)){
               //获取类上的注解
               rateLimit = AnnotationUtils.findAnnotation(handlerMethod.getBean().getClass(), RateLimit.class);

           }
           if(Objects.isNull(rateLimit)){
               return true;
           }
           if (!redisLimiterUtils.tryAcquire(request.getRequestURI(), rateLimit.capacity(), rateLimit.rate())) {
               //抛出请求超时的异常
               throw new TimeoutException();
           }

       }
       return  true;



    }

}
