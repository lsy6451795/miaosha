package com.syliu.miaosha.service;

import com.syliu.miaosha.dao.GoodsDao;
import com.syliu.miaosha.dao.OrderDao;
import com.syliu.miaosha.domain.OrderInfo;
import org.apache.kafka.common.utils.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@EnableScheduling
public class ScedulerService {
    private static Logger logger= LoggerFactory.getLogger(Scheduler.class);
    //使用定时器的方式处理未支付的订单
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private GoodsDao goodsDao;
    //每30分钟触发一次
    @Scheduled(cron = "0 */30 * * * ?")
    private  void schedulerExpireOrders(){

            List<OrderInfo> list=orderDao.getExpiredOrder();
            if(list!=null&&!list.isEmpty()){
                list.stream().forEach(i->{
                    if(i!=null){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-MM-SS");
                        long startDateTime=0 ,endDateTime= 0;
                        try {
                            startDateTime = dateFormat.parse(dateFormat.format(i.getCreateDate())).getTime();
                           endDateTime = dateFormat.parse(dateFormat.format(new Date())).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                       if (((endDateTime - startDateTime) / 43200000)>1){
                            Long id=i.getId();
                            //删除订单，增加库存
                            orderDao.deleteorder(id);
                            orderDao.expire(id);
                            goodsDao.incr(i.getGoodsId());
                       };
                    }
                });
            }

    }



}
