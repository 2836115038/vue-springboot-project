package com.erlang.server.mapper;

import com.erlang.server.entity.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from t_user where username = #{text} or email = #{text}")
    User findUserByNameOrEmail(String text);
}
