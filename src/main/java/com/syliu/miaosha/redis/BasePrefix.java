package com.syliu.miaosha.redis;

public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds;
    private  String prefix;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        this.prefix = prefix;
        this.expireSeconds=0;
    }

    @Override
    public int expireSeconds() {
        //默认0代表永久不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className=getClass().getSimpleName();
        return className+":"+prefix;
    }
}
