package com.syliu.miaosha.RateLimit;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface  RateLimit {
    int capacity() default  20;
    int rate() default  10;
}
