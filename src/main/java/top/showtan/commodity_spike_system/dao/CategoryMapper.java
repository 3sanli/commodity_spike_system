package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.Category;
import top.showtan.commodity_spike_system.vo.CategoryAddVo;
import top.showtan.commodity_spike_system.vo.CategoryGetVo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/31 17:16
 */
@Repository
public interface CategoryMapper {
    @Results(id = "categoryMap", value = {@Result(column = "category_id", property = "id")})
    @Select("select * from category where parent_id=#{categoryId}")
    List<Category> getAllByParentId(Integer categoryId);

    @ResultMap(value = "categoryMap")
    @Select("select * from category where parent_id is null")
    List<Category> getAllInited();

    @Update("update category set category_name=#{category} where category_id=#{id}")
    void modifyById(CategoryGetVo categoryGetVo);

    void deleteByIdList(List<Integer> categoryIds);

    @Options(useGeneratedKeys = true, keyColumn = "category_id", keyProperty = "id")
    @Insert("insert into category (category_name,is_parent,parent_id) values(#{categoryName},#{isParent},#{parentId})")
    void add(CategoryAddVo categoryAddVo);

    //将非父节点修改为父节点
    @Update("update category set is_parent=1 where category_id=#{parentId}")
    void parentNode(Integer parentId);
}
