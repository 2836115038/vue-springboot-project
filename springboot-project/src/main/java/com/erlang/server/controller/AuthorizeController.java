package com.erlang.server.controller;

import com.erlang.server.entity.Response;
import com.erlang.server.service.AuthorizeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Autowired
    private AuthorizeService authorizeService;

    //校验邮箱地址正则表达式
    private final String EMAIL_REGEXP = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    private final String USERNAME_REGEXP = "^[a-zA-Z0-9一-龥]+$";
    private final String PASSWORD_REGEXP = "^[A-Za-z0-9_@]+$";

    @PostMapping("/valid-email")
    public Response<Void> validateEmail(@Pattern (regexp = EMAIL_REGEXP)@RequestParam("email")String email, HttpSession session){

        if (authorizeService.sendEmail(email,session.getId())==null){
            return Response.success("邮件已发送,请注意查收");
        }else {
            return Response.failure(400,authorizeService.sendEmail(email,session.getId()));
        }
    }

    @PostMapping("/register")
    public Response<String> register(
            @Pattern(regexp = USERNAME_REGEXP)@Length(min = 3,max = 10) String username,
            @Pattern(regexp = PASSWORD_REGEXP)@Length(min = 6,max = 16) String password,
            @Pattern(regexp = EMAIL_REGEXP) String email,
            @Length(min = 6,max = 6) String code,
            HttpSession session
    ){
        String sessionId = session.getId();
        if (authorizeService.validateAndRegister(username,password,email,code,sessionId) == null){
            return Response.success("注册成功,请登录");
        }else {
            return Response.failure(400,"注册失败,验证码填写错误");
        }
    }}
