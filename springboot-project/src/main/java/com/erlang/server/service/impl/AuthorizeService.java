package com.erlang.server.service.impl;

import com.erlang.server.entity.domain.User;
import com.erlang.server.mapper.UserMapper;
import com.erlang.server.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthorizeService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //校验用户信息
        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空!");
        }
        User user = userMapper.findUserByNameOrEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名或密码错误!");
        }
        //认证成功返回数据
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("user")
                .build();
    }
}
