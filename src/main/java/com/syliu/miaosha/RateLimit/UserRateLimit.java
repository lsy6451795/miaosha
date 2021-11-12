package com.syliu.miaosha.RateLimit;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface UserRateLimit {
    //令牌桶的容量
    int capacity() default  20;
    //令牌的生成速率，当借口被调用时会产生出发令牌产生
    int rate() default  10;
    String user() default "";
}
