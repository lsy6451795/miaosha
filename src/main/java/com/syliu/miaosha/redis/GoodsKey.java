package com.syliu.miaosha.redis;

public class GoodsKey extends BasePrefix{
    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public GoodsKey(String prefix) {
        super(prefix);
    }
    public  static GoodsKey getGoodList=new GoodsKey(60,"gl");
    public  static GoodsKey getGoodDetail=new GoodsKey(60,"gd");
    public  static GoodsKey getMiaoshaGoodsStock=new GoodsKey(0,"gs");
}
