package com.syliu.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix{

    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public MiaoshaUserKey(String prefix) {
        super(prefix);
    }
    public  static UserKey token=new UserKey(3600*24*2,"token");
    public  static UserKey getById=new UserKey(0,"id");
}
