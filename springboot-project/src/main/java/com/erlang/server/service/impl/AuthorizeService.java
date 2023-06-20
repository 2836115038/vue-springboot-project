package com.erlang.server.service.impl;

import com.erlang.server.entity.domain.User;
import com.erlang.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空!");
        }
        User user = userMapper.findUserByNameOrEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名或密码错误!");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles("user")
                .build();
    }
}
