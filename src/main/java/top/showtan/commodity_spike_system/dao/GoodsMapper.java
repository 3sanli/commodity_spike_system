package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.Goods;
import top.showtan.commodity_spike_system.vo.GoodsSearchVo;
import top.showtan.commodity_spike_system.vo.GoodsVo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/25 22:59
 */
@Repository
public interface GoodsMapper {
    @Options(keyProperty = "id", useGeneratedKeys = true, keyColumn = "goods_id")
    @Insert("insert into goods (goods_name,goods_info,stock,price,creator_id,creator_name) " +
            "values(#{goodsName},#{goodsInfo},#{stock},#{price},#{creatorId},#{creatorName})")
    void add(Goods goods);


    @Results({
            @Result(column = "goods_id", property = "user", one = @One(select = "top.showtan.commodity_spike_system.dao.UserMapper.selectByGoodsId")),
            @Result(column = "goods_id", property = "goodsPicture", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId")),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id,mg.miaosha_stock,mg.miaosha_price,mg.start_time,mg.end_time from goods g left join goods_category gc on g.goods_id=gc.goods_id left join miaosha_goods mg on g.goods_id=mg.goods_id where g.goods_id=#{goodsId}")
    GoodsVo getById(Integer goodsId);

    @Update("update goods set goods_name=#{goodsName},goods_info=#{goodsInfo},stock=#{stock},price=#{price} where goods_id=#{id}")
    void modify(Goods goods);

    @Select("select count(goods_id) from goods where creator_id=#{userId}")
    Long getCountsByCreatorId(Integer userId);

    @Update("update goods set stock=stock-#{goodsCount} where goods_id=#{goodsId} and stock>0")
    void decrStockBy(@Param("goodsId") Integer goodsId, @Param("goodsCount") Integer goodsCount);

    @Select("select stock from goods where goods_id=#{goodsId}")
    Integer getStockById(Integer goodsId);

    @Update("update goods set stock=stock+#{goodsCount} where goods_id=#{goodsId}")
    void addStock(@Param("goodsId") Integer goodsId, @Param("goodsCount") Integer goodsCount);

    @Update("update goods set stock=100")
    void initAllGoodsStock();


    @Results({
            @Result(column = "goods_id", property = "goodsPicture", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId", fetchType = FetchType.EAGER)),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id from goods g left join goods_category gc on g.goods_id=gc.goods_id where g.creator_id=#{userId} order by created_time desc limit #{offset},#{take}")
    List<GoodsVo> getByCreatorId(@Param("userId") Integer userId, @Param("offset") Long offset, @Param("take") Integer take);

    @Delete("delete from goods where goods_id=#{goodsId}")
    void deleteById(Integer goodsId);

    @Results({
            @Result(column = "goods_id", property = "goodsPicture", one = @One(select = "top.showtan.commodity_spike_system.dao.PictureMapper.getByGoodsId", fetchType = FetchType.EAGER)),
            @Result(column = "goods_id", property = "id")
    })
    @Select("select g.*,gc.category_id from goods g left join goods_category gc on g.goods_id=gc.goods_id where g.creator_id=#{userId} order by created_time desc")
    List<GoodsVo> getAllByCreatorId(Integer userId);

    //xml实现
    Long getSimpleCountsBySearchVo(GoodsSearchVo searchVo);

    Long getMiaoshaCountsBySearchVo(GoodsSearchVo searchVo);

    List<GoodsVo> listSimpleBySearchVo(@Param("searchVo") GoodsSearchVo searchVo,@Param("offset") Long offset, @Param("take") Integer take);

    List<GoodsVo> listMiaoshaBySearchVo(@Param("searchVo") GoodsSearchVo searchVo,@Param("offset") Long offset, @Param("take") Integer take);
}
