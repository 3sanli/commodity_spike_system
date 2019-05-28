package top.showtan.commodity_spike_system.dao;


import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.MiaoshaGoods;
import top.showtan.commodity_spike_system.entity.MiaoshaOrder;
import top.showtan.commodity_spike_system.vo.GoodsVo;
import top.showtan.commodity_spike_system.vo.OrderVo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/26 19:44
 */
@Repository
public interface MiaoshaMapper {
    @Options(keyProperty = "id", useGeneratedKeys = true, keyColumn = "miaosha_goods_id")
    @Insert("insert into miaosha_goods (goods_id,miaosha_stock,miaosha_price,start_time,end_time) " +
            "values(#{goodsId},#{miaoshaStock},#{miaoshaPrice},#{startTime},#{endTime})")
    void add(MiaoshaGoods miaoshaGoods);

    @Update("update miaosha_goods set miaosha_price=#{miaoshaPrice},miaosha_stock=#{miaoshaStock},start_time=#{startTime},end_time=#{endTime} where goods_id=#{goodsId}")
    void modify(MiaoshaGoods miaoshaGoods);

    @Select("select miaosha_stock from miaosha_goods where goods_id=#{goodsId}")
    Integer getStockById(Integer goodsId);

    @Update("update miaosha_goods set miaosha_stock=miaosha_stock-1 where goods_id=#{goodsId} and miaosha_stock>0")
    void decrStock(Integer goodsId);

    @Insert("insert into miaosha_order (user_id,goods_id,order_id) values(#{userId},#{goodsId},#{orderId})")
    void doAdd(MiaoshaOrder miaoshaOrder);

    @Results({
            @Result(property = "user", column = "user_id", one = @One(select = "top.showtan.commodity_spike_system.dao.UserMapper.selectById")),
            @Result(property = "goodsPicture", column = "goods_id", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId")),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id,mg.miaosha_stock,mg.miaosha_price,mg.start_time,mg.end_time from goods g left join goods_category gc on g.goods_id=gc.goods_id right join miaosha_goods mg on g.goods_id=mg.goods_id where g.goods_id=#{goodsId}")
    GoodsVo getByGoodsId(Integer goodsId);

    @Select("select miaosha_stock from miaosha_goods where goods_id=#{goodsId}")
    Integer getStockByGoodsId(Integer goodsId);

    @Delete("delete from miaosha_order")
    void cleanAll();

    @Update("update miaosha_goods set miaosha_stock=50")
    void initAllMiaoshaGoodsStock();

    @Results({
            @Result(column = "order_id", property = "id")
    })
    @Select("select oi.* from miaosha_order mo left join order_info oi on mo.order_id=oi.order_id where mo.user_id=#{userId} and mo.goods_id=#{goodsId}")
    OrderVo getByUserIdAndGoodsId(@Param("userId") Integer userId, @Param("goodsId") Integer goodsId);

    @Results({
            @Result(column = "goods_id", property = "id")
    })
    @Select("select goods_id,miaosha_stock from miaosha_goods")
    List<GoodsVo> getAllStock();

    @Select("select count(miaosha_goods_id) from miaosha_goods where goods_id=#{goodsId}")
    Integer countsByGoodsId(Integer goodsId);

    @Select("select count(miaosha_goods_id) from miaosha_goods mg left join goods g on mg.goods_id=g.goods_id where g.creator_id=#{userId}")
    Long getCountsByCreatorId(Integer userId);

    @Results({
            @Result(property = "user", column = "user_id", one = @One(select = "top.showtan.commodity_spike_system.dao.UserMapper.selectById")),
            @Result(property = "goodsPicture", column = "goods_id", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId")),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id,mg.miaosha_stock,mg.miaosha_price,mg.start_time,mg.end_time from goods g left join goods_category gc on g.goods_id=gc.goods_id right join miaosha_goods mg on g.goods_id=mg.goods_id where g.creator_id=#{userId} order by g.created_time desc limit #{offset},#{take}")
    List<GoodsVo> getByCreatorId(@Param("userId") Integer userId, @Param("offset") Long offset, @Param("take") Integer take);

    @Delete("delete from miaosha_goods where goods_id=#{goodsId}")
    void over(Integer goodsId);

    @Select("select count(miaosha_goods_id) from miaosha_goods")
    Long countsAll();

    @Results({
            @Result(property = "user", column = "goods_id", one = @One(select = "top.showtan.commodity_spike_system.dao.UserMapper.selectByGoodsId")),
            @Result(property = "goodsPicture", column = "goods_id", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId")),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id,mg.miaosha_price from goods g left join goods_category gc on g.goods_id=gc.goods_id right join miaosha_goods mg on g.goods_id=mg.goods_id order by mg.end_time asc limit #{offset},#{take}")
    List<GoodsVo> search(@Param("offset") Long offset, @Param("take") Integer take);

    @Results({
            @Result(property = "user", column = "user_id", one = @One(select = "top.showtan.commodity_spike_system.dao.UserMapper.selectById")),
            @Result(property = "goodsPicture", column = "goods_id", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId")),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id,mg.miaosha_stock,mg.miaosha_price,mg.start_time,mg.end_time from goods g left join goods_category gc on g.goods_id=gc.goods_id right join miaosha_goods mg on g.goods_id=mg.goods_id where g.creator_id=#{userId} order by g.created_time desc")
    List<GoodsVo> getAllByCreatorId(Integer userId);
}
