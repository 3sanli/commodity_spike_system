package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.Picture;

/**
 * @Author: sanli
 * @Date: 2019/5/3 11:21
 */
@Repository
public interface GoodsPictureMapper {
    @Delete("delete from goods_picture where goods_id=#{goodsId}")
    void deleteByGoodsId(Integer goodsId);

    @Insert("insert into goods_picture (goods_id,picture_id) values(#{goodsId},#{pictureId})")
    void addGoodsPicture(@Param("goodsId") Integer goodsId, @Param("pictureId") Integer pictureId);

    @Update("update goods_picture set picture_id=#{pictureId} where goods_id=#{goodsId}")
    void changePicture(@Param("goodsId") Integer goodsId, @Param("pictureId") Integer pictureId);

    @Results({
            @Result(column = "picture_id", property = "id")
    })
    @Select("select p.* from goods_picture gp left join picture p on gp.picture_id=p.picture_id where gp.goods_id=#{goodsId}")
    Picture getByGoodsId(Integer goodsId);
}
