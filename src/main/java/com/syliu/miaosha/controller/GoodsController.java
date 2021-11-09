package com.syliu.miaosha.controller;

import com.syliu.miaosha.Result.Result;
import com.syliu.miaosha.domain.MiaoshaUser;
import com.syliu.miaosha.redis.GoodsKey;
import com.syliu.miaosha.redis.RedisService;
import com.syliu.miaosha.service.GoodsService;
import com.syliu.miaosha.service.MiaoshaUserService;
import com.syliu.miaosha.vo.GoodsDetailVo;
import com.syliu.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static Logger logger= LoggerFactory.getLogger(LoggerFactory.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private MiaoshaUserService miaoshaUserService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;


    /*
    * QPS:2472
    * 5000*10
    * */
    @RequestMapping("/to_list")
    @ResponseBody
    public String to_list( HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user){
        List<GoodsVo> goodsVos = goodsService.listGoodsVo();
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsVos);
//        return "goods_list";
        //取缓存
        String html=redisService.get(GoodsKey.getGoodList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //手动渲染
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodList, "", html);
        }
        return html;
    }
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId){

        GoodsVo goods=goodsService.listGoodsVoByGoodesId(goodsId);
        long endAt=goods.getEndDate().getTime();
        long startAt = goods.getStartDate().getTime();
        long now=System.currentTimeMillis();
        int miaoshaStatus=0;
        int remainSeconds=0;
        if(now<startAt){
            miaoshaStatus=0;
            remainSeconds=(int)(startAt-now)/1000;
        }
        else if(now>endAt){
            miaoshaStatus=2;
            remainSeconds=-1;
        }
        else {
            miaoshaStatus=1;
            remainSeconds=0;

        }
        GoodsDetailVo goodsDetailVo=new GoodsDetailVo();
        goodsDetailVo.setUser(user);
        goodsDetailVo.setGoods(goods);
       goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
       goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);
    }
    @RequestMapping(value="/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String detail2( HttpServletRequest request, HttpServletResponse response,Model model,MiaoshaUser user,@PathVariable("goodsId")long goodsId){

        model.addAttribute("user",user);
        //取缓存
        String html=redisService.get(GoodsKey.getGoodDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goods=goodsService.listGoodsVoByGoodesId(goodsId);
        long endAt=goods.getEndDate().getTime();
        long startAt = goods.getStartDate().getTime();
        long now=System.currentTimeMillis();
        int miaoshaStatus=0;
        int remainSeconds=0;
        if(now<startAt){
            miaoshaStatus=0;
            remainSeconds=(int)(startAt-now)/1000;
        }
        else if(now>endAt){
            miaoshaStatus=2;
            remainSeconds=-1;
        }
        else {
            miaoshaStatus=1;
            remainSeconds=0;

        }
//        GoodsDetailVo vo=new GoodsDetailVo();

        model.addAttribute("goods",goods);
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
//        return "goods_detail";
        WebContext ctx = new WebContext(request,response,
                request.getServletContext(),request.getLocale(), model.asMap());
        html=thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodDetail, ""+goodsId, html);
        }
        return html;
    }


}
