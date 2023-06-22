package com.erlang.server.service.impl;

import com.erlang.server.entity.domain.User;
import com.erlang.server.mapper.UserMapper;
import com.erlang.server.service.AuthorizeService;
import com.erlang.server.utils.DB2RedisCache;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private DB2RedisCache redisCache;

    @Value("${spring.mail.username}")
    String from;

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

    @Override
    public Boolean sendEmail(String email, String sessionId) {
        /*
        1.生成对应的验证码
        2.将邮箱对应的验证码存放到redis(过期时间5分钟)
        3.若此时要重发验证码,若剩余时间低于4分钟,可以重新发送一次,同时删除redis之前存储的键值对
        4.发送验证码到指定邮箱
        5.如果发送失败,将redis插入的键值对删除
        6.用户注册时,从redis取出对应键值对,然后看验证码是否一致
        */

        String key = "email:"+email+":"+sessionId;
        if (Boolean.TRUE.equals(redisCache.hasKey(key))){
            Long expire = redisCache.getExpire(key, TimeUnit.SECONDS);
            if (expire >240){
                return false;
            }
        }
        int code = new Random().nextInt(899999)+100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        //邮件标题
        message.setSubject("验证邮件");
        message.setText("尊敬的网站用户,您的验证码是:"+code+",验证码有效期为5分钟,请勿将验证码泄露他人!");
        try {
            //发送邮件
            mailSender.send(message);
            //将邮件信息存储到redis

            redisCache.set(key,String.valueOf(code));
            redisCache.expire(key,5L, TimeUnit.MINUTES);
            return true;
        } catch (MailException e){
            e.printStackTrace();
            return false;
        }
    }
}
