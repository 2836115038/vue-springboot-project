package com.erlang.server.controller;

import com.erlang.server.entity.Response;
import com.erlang.server.service.AuthorizeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
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

    @PostMapping("/valid-email")
    public Response<Void> validateEmail(@Pattern (regexp = EMAIL_REGEXP)@RequestParam("email")String email, HttpSession session){
        if (authorizeService.sendEmail(email,session.getId())){
            return Response.success("邮件已发送,请注意查收");
        }else {
            return Response.failure(400,"邮件发送失败,请联系管理员");
        }
    }
}
