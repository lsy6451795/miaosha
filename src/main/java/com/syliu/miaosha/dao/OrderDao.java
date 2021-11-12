package com.syliu.miaosha.dao;

import com.syliu.miaosha.domain.MiaoshaOrder;
import com.syliu.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDao {
    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long id, @Param("goodsId") long goodsId);
    @Insert("insert into order_info(user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insert(OrderInfo orderInfo);
    @Insert("insert into miaosha_order(user_id,goods_id,order_id)values(#{userId},#{goodsId},#{orderId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);
    @Select("Select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
    @Select("Select * from order_info where status=0")
    List<OrderInfo> getExpiredOrder();
    @Delete("Delete from order_info where id=#{id}")
    void expire(Long id);
    @Delete("Delete from miaosha_order where order_id=#{id}")
    void deleteorder(Long id);
}
