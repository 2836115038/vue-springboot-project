package com.erlang.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erlang.server.entity.domain.User;
import com.erlang.server.entity.domain.UserVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from t_user where username = #{text} or email = #{text}")
    User findUserByNameOrEmail(String text);

    @Select("select * from t_user where username = #{text} or email = #{text}")
    UserVo findUserVoByName(String text);


    @Insert("insert into t_user(username,password,email) values(#{username},#{password},#{email})")
    Integer creatAccount(String username,String password,String email);

    @Update("update t_user set password = #{password} where email = #{email}")
    Integer resetPasswordByEmail(String password,String email);

}
