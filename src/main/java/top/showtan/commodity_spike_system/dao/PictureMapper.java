package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.Picture;


/**
 * @Author: sanli
 * @Date: 2019/3/28 14:30
 */
@Repository
public interface PictureMapper {
    @Results({
            @Result(column = "picture_id", property = "id")
    })
    @Select("select p.* from picture p left join goods_picture gp on p.picture_id=gp.picture_id where gp.goods_id=#{goodsId}")
    Picture getByGoodsId(Integer goodsId);

    @Options(keyProperty = "id", keyColumn = "picture_id", useGeneratedKeys = true)
    @Insert("insert into picture(picture_address) values(#{pictureAddress})")
    void add(Picture picture);
}
