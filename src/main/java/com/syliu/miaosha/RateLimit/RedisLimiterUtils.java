package com.syliu.miaosha.RateLimit;

import com.alibaba.fastjson.JSONObject;
import com.syliu.miaosha.MainApplication;
import com.syliu.miaosha.config.ApplicationContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;


import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

import static com.syliu.miaosha.redis.RedisService.returnToPool;

/**
 * @Description 限流工具类
 * @Author CJB
 * @Date 2020/3/19 17:21
 */
@Component
public class RedisLimiterUtils {
    @Autowired
    private JedisPool jedisPool;
    /**
     * lua脚本，限流
     */
    private final static String TEXT="local ratelimit_info = redis.pcall('HMGET',KEYS[1],'last_time','current_token')\n" +
            "local last_time = ratelimit_info[1]\n" +
            "local current_token = tonumber(ratelimit_info[2])\n" +
            "local max_token = tonumber(ARGV[1])\n" +
            "local token_rate = tonumber(ARGV[2])\n" +
            "local current_time = tonumber(ARGV[3])\n" +
            "if current_token == nil then\n" +
            "  current_token = max_token\n" +
            "  last_time = current_time\n" +
            "else\n" +
            "  local past_time = current_time-last_time\n" +
            "  \n" +
            "  if past_time>1000 then\n" +
            "\t  current_token = current_token+token_rate\n" +
            "\t  last_time = current_time\n" +
            "  end\n" +
            "\n" +
            "  if current_token>max_token then\n" +
            "    current_token = max_token\n" +
            "\tlast_time = current_time\n" +
            "  end\n" +
            "end\n" +
            "\n" +
            "local result = 0\n" +
            "if(current_token>0) then\n" +
            "  result = 1\n" +
            "  current_token = current_token-1\n" +
            "  last_time = current_time\n" +
            "end\n" +
            "redis.call('HMSET',KEYS[1],'last_time',last_time,'current_token',current_token)\n" +
            "return result";

    /**
     * 获取令牌
     * @param key 请求id
     * @param max 最大能同时承受多少的并发（桶容量）
     * @param rate  每秒生成多少的令牌
     * @return 获取令牌返回true，没有获取返回false
     */

    public boolean tryAcquire(String key, int max,int rate) {

//        List<String> keyList = new ArrayList<>(1);
//        keyList.add(realkey);
        String realkey=key+":limit";
        Long eval =0L;
        Jedis resource = null;
        try{
            resource=jedisPool.getResource();
            eval=(Long) resource.eval(TEXT, 1, realkey, Integer.toString(max), Integer.toString(rate), Long.toString(System.currentTimeMillis()));
            if(eval==1)
            return true;
            else return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        finally {
            returnToPool(resource);
        }
//        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
//        script.setResultType(Long.class);
//        script.setScriptText(TEXT);
//        return Long.valueOf(1).equals(stringRedisTemplate.execute(script,keyList,Integer.toString(max), Integer.toString(rate),
//                Long.toString(System.currentTimeMillis())));
    }
}
