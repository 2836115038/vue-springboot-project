package com.erlang.server.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    /*
    发送电子邮件
     */
    Boolean sendEmail(String email, String sessionId);
}
