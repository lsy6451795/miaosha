package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.CodeMsg;
import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.MiaoshaOrder;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.domain.OrderInfo;
import com.syliu.miaosha.kafka.MQProducer;
import com.syliu.miaosha.rabbitmq.MQSender;
import com.syliu.miaosha.rabbitmq.MiaoshaMessage;
import com.syliu.miaosha.redis.GoodsKey;
import com.syliu.miaosha.redis.MiaoshaKey;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.GoodsService;
import com.syliu.miaosha.service.MiaoshaService;
import com.syliu.miaosha.service.OrderService;
import com.syliu.miaosha.util.MD5Util;
import com.syliu.miaosha.util.UUIDUtil;
import com.syliu.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender sender;
    @Autowired
    private MQProducer producer;
    private Map<Long,Boolean> localOverMap=new HashMap<Long,Boolean>();
    /*QPS：4600 1000*50
    * QPS：5527 1000*50*/
    /*
    * 系统初始化*/
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList==null){
            return;
        }
        for(GoodsVo goodsVo:goodsVoList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> do_miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId,
                                      @PathVariable("path")String path){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check=miaoshaService.checkPath(user,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEAGLE);
        }

        Boolean isOver = localOverMap.get(goodsId);
        if(isOver)
            return Result.error(CodeMsg.SESSION_ERROR);

        Long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if(stock<=0){
            localOverMap.put(goodsId,true);
//          model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
          return Result.error(CodeMsg.SECKILL_OVER);
//          return "miaosha_fail";
      }
        //判断商品是否秒杀到
        MiaoshaOrder miaoshaOrder=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
      if (miaoshaOrder!=null){
//          model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
//          return  "miaosha_fail";
          return Result.error(CodeMsg.REPEATE_SECKILL);
      }
        MiaoshaMessage miaoshaMessage=new MiaoshaMessage();
      miaoshaMessage.setGoodsId(goodsId);
      miaoshaMessage.setUser(user);


      //rabbitmq
        //    sender.sendMiaoshaMessage(miaoshaMessage);
        producer.sendMiaoshaMessage(miaoshaMessage);
      return Result.success(0);//  排队中
//      GoodsVo goodsVo= goodsService.getGoodsVoByGoodsId(goodsId);
//      int stock=goodsVo.getGoodsStock();
//      if(stock<=0){
////          model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
//          return Result.error(CodeMsg.SECKILL_OVER);
////          return "miaosha_fail";
//      }
//      //判断商品是否秒杀到
//        MiaoshaOrder miaoshaOrder=orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
//      if (miaoshaOrder!=null){
////          model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
////          return  "miaosha_fail";
//          return Result.error(CodeMsg.REPEATE_SECKILL);
//      }
      //减库存 下订单

//       OrderInfo orderInfo= miaoshaService.miaosha(user,goodsVo);

//      model.addAttribute("orderInfo",orderInfo);
//      model.addAttribute("goods",goodsVo);
//      return Result.success(orderInfo);
//      return "order_detail";
    }

    @RequestMapping("/result")
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(Model model, MiaoshaUser user,
                                         @RequestParam("goodsId")long goodsId,@RequestParam("verifyCode")int verifyCode){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean check=miaoshaService.checkVerify(user,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String path=miaoshaService.createMiaoshaPath(user,goodsId);

        return Result.success(path);
    }
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse httpServletResponse,Model model, MiaoshaUser user, @RequestParam("goodsId")long goodsId){
        model.addAttribute("user",user);
        if(user==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image=miaoshaService.createVerifyCodeImage(user,goodsId);
        try{
            OutputStream out=httpServletResponse.getOutputStream();
            ImageIO.write(image,"JPEG",out);
           out.flush();
           out.close();
           return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }

    }
}
