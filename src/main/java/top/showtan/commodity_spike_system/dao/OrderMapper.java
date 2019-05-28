package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.OrderInfo;
import top.showtan.commodity_spike_system.vo.OrderSearchVo;
import top.showtan.commodity_spike_system.vo.OrderVo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/4/1 20:05
 */
@Repository
public interface OrderMapper {
    @Options(keyProperty = "id", keyColumn = "order_id", useGeneratedKeys = true)
    @Insert("insert into order_info (user_id,goods_id,goods_name,goods_count,goods_price) " +
            "values(#{userId},#{goodsId},#{goodsName},#{goodsCount},#{goodsPrice})")
    void add(OrderInfo orderInfo);

    @Update("update order_info set status=2 where order_id=#{orderId}")
    void cancel(Integer orderId);

    @Delete("delete from  order_info")
    void cleanAll();

    @Select("select count(order_id) from order_info where goods_id=#{goodsId}")
    Integer countsSimpleByGoodsId(Integer goodsId);

    @Select("select count(miaosha_order_id) from miaosha_order where goods_id=#{goodsId}")
    Integer countMiaoshaByGoodsId(Integer goodsId);

    @Select("select count(order_id) from order_info where user_id=#{userId}")
    Long getCountsByUserId(Integer userId);

    @Results(id = "orderMap",
            value = {
                    @Result(column = "order_id", property = "id")
            })
    @Select("select oi.*,mo.miaosha_order_id from order_info oi left join miaosha_order mo on oi.order_id=mo.order_id where oi.user_id=#{userId} order by oi.created_time desc limit #{offset},#{take}")
    List<OrderVo> listByUserId(@Param("userId") Integer userId, @Param("offset") Long offset, @Param("take") Integer take);

    @ResultMap(value = "orderMap")
    @Select("select oi.*,mo.miaosha_order_id from order_info oi left join miaosha_order mo on oi.order_id=mo.order_id where oi.order_id=#{orderId}")
    OrderVo getByOrderId(Integer orderId);

    @Delete("delete from order_info where order_id=#{orderId}")
    void deleteById(Integer orderId);

    //xml实现
    List<OrderVo> listSimpleBySearchVo(@Param("searchVo") OrderSearchVo searchVo, @Param("offset") Long offset, @Param("take") Integer take);

    List<OrderVo> listMiaoshaBySearchVo(@Param("searchVo") OrderSearchVo searchVo, @Param("offset") Long offset, @Param("take") Integer take);

    Long getSimpleCountsBySearchVo(OrderSearchVo searchVo);

    Long getMiaoshaCountsBySearchVo(OrderSearchVo searchVo);

    @Select("select goods_id from order_info where goods_id not in(select goods_id from miaosha_order)")
    List<OrderVo> searchSimple();

    @Select("select goods_id,user_id from miaosha_order")
    List<OrderVo> searchMiaosha();
}
