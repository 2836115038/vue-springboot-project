package com.erlang.server.service.impl;

import com.erlang.server.entity.domain.User;
import com.erlang.server.mapper.UserMapper;
import com.erlang.server.service.AuthorizeService;
import com.erlang.server.utils.DB2RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl  implements AuthorizeService {

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
    public String sendEmail(String email, String sessionId,Boolean hasUser) {
        /*
        1.生成对应的验证码
        2.将邮箱对应的验证码存放到redis(过期时间5分钟)
        3.若此时要重发验证码,若剩余时间低于4分钟,可以重新发送一次,同时删除redis之前存储的键值对
        4.发送验证码到指定邮箱
        5.如果发送失败,将redis插入的键值对删除
        6.用户注册时,从redis取出对应键值对,然后看验证码是否一致
        */

        String key = "email:"+email+":"+sessionId+":"+hasUser;
        if (Boolean.TRUE.equals(redisCache.hasKey(key))){
            Long expire = redisCache.getExpire(key, TimeUnit.SECONDS);
            if (expire >240) return "请求频繁,请稍后再试!";
        }
        User account = userMapper.findUserByNameOrEmail(email);
        if (hasUser && account == null) return "没有此邮件地址的账户";
        if (!hasUser && account !=null) return "此邮箱已被其他用户注册!";


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
            return null;
        } catch (MailException e){
            e.printStackTrace();
            return "邮件发送失败,请检查邮件地址是否有效!";
        }
    }

    /**
     * 校验用户信息完成注册
     * @param username
     * @param password
     * @param email
     * @param code
     * @return
     */
    @Override
    public String validateAndRegister(String username, String password, String email, String code,String sessionId) {
        String key = "email:"+email+":"+sessionId+":false";
        if (Boolean.TRUE.equals(redisCache.hasKey(key))){
            String s = redisCache.get(key).toString();
            if (Objects.isNull(s)) return "验证码失效,请重新发送";
            if (s.equals(code)){
                User user = userMapper.findUserByNameOrEmail(username);
                if (user !=null) return "此用户名已被注册!";
                redisCache.delete(key);
                //密码加密处理
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String encodePassword = passwordEncoder.encode(password);
                if (userMapper.creatAccount(username,encodePassword,email) > 0){
                    return null;
                }else {
                    return "内部错误,请联系管理员!";
                }
            }else {
                return "验证码错误,请重新输入";
            }
        }else {
            return "请先发送验证码";
        }
    }

    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:"+email+":"+sessionId+":true";
        if (Boolean.TRUE.equals(redisCache.hasKey(key))){
            String s = redisCache.get(key).toString();
            if (Objects.isNull(s)) return "验证码失效,请重新发送";
            if (s.equals(code)){
                redisCache.delete(key);
                return null;
            }else {
                return "验证码错误,请重新输入";
            }
        }else {
            return "请先发送验证码";
        }
    }

    @Override
    public Boolean resetPassWord(String password, String email) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return userMapper.resetPasswordByEmail(passwordEncoder.encode(password),email)>0;
    }
}
