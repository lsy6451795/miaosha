package com.syliu.miaosha.redis;

import com.alibaba.fastjson.JSON;
import com.syliu.miaosha.domain.MiaoshaUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;



@Service
public class RedisService {
//    public static void main(String[] args) {
//        JSONObject jsonObject= (JSONObject)JSONObject.toJSON(new User(1, "111"));
//        User user = JSON.toJavaObject(jsonObject, User.class);
//        System.out.println(user.getId()+user.getName());
//    }
    @Autowired
    private  JedisPool jedisPool;
    public  <T> T get(KeyPrefix prefix, String key, Class<T> clazz){

        Jedis resource = null;
        try{

            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;

            String str=resource.get(realKey);
            T t=stringToBean(str,clazz);
            return t;
        }finally {
            returnToPool(resource);
        }
    }
    public  <T> boolean set(KeyPrefix prefix, String key,T value){
        Jedis resource = null;
        try{

            resource=jedisPool.getResource();
            String str=beanToString(value);
            String realKey=prefix.getPrefix()+key;
            if(str==null||str.length()<=0)return false;
            int seconds=prefix.expireSeconds();
            if(seconds<=0) {
                resource.set(realKey, str);
            }else{
                resource.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(resource);
        }
    }
    public  <T> boolean exists(KeyPrefix prefix, String key){
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return  resource.exists(realKey);
        }finally {
            returnToPool(resource);
        }
    }

    public  <T> boolean delete(KeyPrefix prefix, String key){
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            Long del = resource.del(realKey);
            return del>0;
        }finally {
            returnToPool(resource);
        }
    }
    public  <T> Long incr(KeyPrefix prefix, String key){
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return  resource.incr(realKey);
        }finally {
            returnToPool(resource);
        }
    }
    public  <T> Long decr(KeyPrefix prefix, String key){
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+key;
            return  resource.decr(realKey);
        }finally {
            returnToPool(resource);
        }
    }
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if(clazz == int.class || clazz == Integer.class) {
            return (T)Integer.valueOf(str);
        }else if(clazz == String.class) {
            return (T)str;
        }else if(clazz == long.class || clazz == Long.class) {
            return  (T)Long.valueOf(str);
        }else {


            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }



    public static <T> String beanToString(T value) {
        if(value==null)return null;
        Class<?>clazz=value.getClass();
        if(clazz==int.class||clazz==Integer.class){
            return ""+value;
        }
        else if(clazz==String.class)return (String)value;
        else if(clazz==long.class||clazz==Long.class)return ""+value;
        else return JSON.toJSONString(value);
    }

    public static void returnToPool(Jedis resource) {
        if(resource!=null){
            resource.close();
        }
    }


    public MiaoshaUser get(String token) {
        if(StringUtils.isEmpty(token))return null;
        return get(MiaoshaUserKey.token,token,MiaoshaUser.class);
    }

    public void epire(KeyPrefix prefix, String token){
        int time=MiaoshaUserKey.token.expireSeconds();
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            String realKey=prefix.getPrefix()+token;
            System.out.println(realKey);
            resource.expire(realKey,time);
        }finally {
            returnToPool(resource);
        }
    }
}
