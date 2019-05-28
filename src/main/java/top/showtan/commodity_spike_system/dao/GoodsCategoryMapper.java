package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @Author: sanli
 * @Date: 2019/5/3 11:18
 */
@Repository
public interface GoodsCategoryMapper {
    @Delete("delete from goods_category where goods_id=#{goodsId}")
    void deleteByGoodsId(Integer goodsId);

    @Insert("insert into goods_category (goods_id,category_id) values(#{goodsId},#{categoryId})")
    void addGoodsCategory(@Param("goodsId") Integer goodsId, @Param("categoryId") Integer categoryId);

    @Update("update goods_category set category_id=#{category} where goods_id=#{goodsId}")
    void changeCategory(@Param("goodsId") Integer goodsId, @Param("category") Integer categoryId);
}
