package com.syliu.miaosha.service;

import com.syliu.miaosha.dao.GoodsDao;
import com.syliu.miaosha.domain.Goods;
import com.syliu.miaosha.domain.MiaoshaOrder;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.domain.OrderInfo;
import com.syliu.miaosha.redis.MiaoshaKey;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.util.MD5Util;
import com.syliu.miaosha.util.UUIDUtil;
import com.syliu.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private  OrderService orderService;
    @Autowired
    private RedisService redisService;


    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user==null||path==null)
            return false;
        String PathOld= redisService.get(MiaoshaKey.getMiaoshaPath,""+user.getId()+""+goodsId,String.class);
        if(PathOld.equals(path)){
            return true;
        }
        return false;
    }

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        boolean success = goodsService.reducestock(goods);
        if(success) {
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }

    }
    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
    }
    public long getMiaoshaResult(Long id, long goodsId) {
        MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(id,goodsId);
        if(order!=null){
            return order.getOrderId();
        }else {
            boolean isOver=getGoodsOver(goodsId);
            if(isOver){
                return -1;
            }
            else
                return 0;
        }
    }

    public String createMiaoshaPath(MiaoshaUser user,Long goodsId) {
        String str= MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPath,""+user.getId()+""+goodsId,str);
        return str;
    }
     //验证码实现
    public BufferedImage createVerifyCodeImage(MiaoshaUser user, long goodsId) {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = createVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        try {
            ScriptEngineManager scriptEngineManager=new ScriptEngineManager();
            ScriptEngine engine = scriptEngineManager.getEngineByName("JavaScript");
            return (Integer)engine.eval(verifyCode);
        }catch (Exception e){
           e.printStackTrace();
           return 0;
        }
    }

    private static  char[] ops=new char[]{'+','-','*'};
    public String createVerifyCode(Random rdm) {
        int n1 = rdm.nextInt(10);
        int n2 = rdm.nextInt(10);
        int n3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp=""+n1+op1+n2+op2+n3;
        return exp;
    }

    public boolean checkVerify(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user==null)
            return false;
        Integer integerCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (integerCode==null||integerCode-verifyCode!=0)return false;

        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}
