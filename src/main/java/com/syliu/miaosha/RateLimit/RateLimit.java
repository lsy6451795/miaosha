package com.syliu.miaosha.RateLimit;

import com.syliu.miaosha.domain.MiaoshaUser;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface  RateLimit {
    //令牌桶的容量
    int capacity() default  1000;
    //令牌的生成速率，当借口被调用时会产生出发令牌产生
    int rate() default  50;
}
