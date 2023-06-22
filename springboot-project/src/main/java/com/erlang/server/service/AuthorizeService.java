package com.erlang.server.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    /*
    发送电子邮件
     */
    String sendEmail(String email, String sessionId);

    String validateAndRegister(String username,String password,String email,String code,String sessionId);
}
