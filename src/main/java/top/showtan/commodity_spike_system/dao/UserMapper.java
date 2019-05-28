package top.showtan.commodity_spike_system.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import top.showtan.commodity_spike_system.entity.User;
import top.showtan.commodity_spike_system.vo.UserVo;

import java.util.List;

/**
 * @Author: sanli
 * @Date: 2019/3/19 22:33
 */
@Repository
public interface UserMapper {
    @Results(id = "userMap", value = {
            @Result(column = "user_id", property = "id")
    })
    @Select("select * from user where mobile=#{mobile}")
    User selectByMobie(@Param("mobile") String mobile);

    @ResultMap(value = "userMap")
    @Select("select * from user where user_id=#{id}")
    User selectById(Integer id);

    @ResultMap(value = "userMap")
    @Select("select u.* from user u left join goods g on g.creator_id=u.user_id where g.goods_id=#{goodsId}")
    User selectByGoodsId(Integer goodsId);

    @Update("update user set mobile=#{mobile},nick_name=#{nickName},password=#{password},icon_id=#{iconId} where user_id=#{id}")
    void modify(User user);

    //    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("insert into user (user_id,mobile,nick_name,password) values(#{id},#{mobile},#{nickName},#{password})")
    void addWithId(User user);

    @Options(keyColumn = "user_id",keyProperty = "id",useGeneratedKeys = true)
    @Insert("insert into user (mobile,nick_name,password) values(#{mobile},#{nickName},#{password})")
    void addWithOutId(User user);

    @ResultMap(value = "userMap")
    @Select({"select * from user"})
    List<User> getAll();

    @Delete("delete from user where user_id!=1")
    void cleanAllForTest();

    @Select("select count(user_id) from user")
    Long getCounts();

    @ResultMap("userMap")
    @Select("select * from user order by user_id asc limit #{offset},#{take}")
    List<UserVo> search(@Param("offset") Long offset, @Param("take") Integer take);

    @Delete("delete from user where user_id=#{userId}")
    void deleteById(Integer userId);
}
