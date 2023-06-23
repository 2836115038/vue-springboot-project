package com.erlang.server.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    /*
    发送电子邮件
     */
    String sendEmail(String email, String sessionId,Boolean hasUser);

    String validateAndRegister(String username,String password,String email,String code,String sessionId);

    String validateOnly(String email,String code,String sessionId);

    Boolean resetPassWord(String password,String email);
}
