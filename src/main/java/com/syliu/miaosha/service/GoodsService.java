package com.syliu.miaosha.service;

import com.syliu.miaosha.dao.GoodsDao;
import com.syliu.miaosha.domain.Goods;
import com.syliu.miaosha.domain.MiaoshaGoods;
import com.syliu.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }
    public GoodsVo listGoodsVoByGoodesId(long goodsid){
        return  goodsDao.listGoodsVoByGoodsId(goodsid);
    }


    public GoodsVo getGoodsVoByGoodsId(long goodsId) {

        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reducestock(GoodsVo goodsVo) {
        MiaoshaGoods goods=new MiaoshaGoods();
        goods.setGoodsId(goodsVo.getId());

        int ret=goodsDao.reduceStock(goods);
        return ret > 0;
    }
}
