package com.syliu.miaosha.config;

import com.syliu.miaosha.RateLimit.RateLimiterIntercept;
import com.syliu.miaosha.RateLimit.UserRateLimiterIntercept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private  UserArgumentResolver userArgumentResolver;
    @Autowired
    private RateLimiterIntercept rateLimiterIntercept;
    @Autowired
    private UserRateLimiterIntercept userRateLimiterIntercept;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(rateLimiterIntercept);
        registry.addInterceptor(userRateLimiterIntercept);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
      resolvers.add(userArgumentResolver);

    }
}
