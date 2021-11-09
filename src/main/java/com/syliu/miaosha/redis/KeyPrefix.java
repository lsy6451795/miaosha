package com.syliu.miaosha.redis;

public interface KeyPrefix {
    public int expireSeconds();
    String getPrefix();
}
