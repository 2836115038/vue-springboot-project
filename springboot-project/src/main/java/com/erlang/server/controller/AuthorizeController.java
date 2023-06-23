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
    public Response<Void> validateRegisterEmail(@Pattern (regexp = EMAIL_REGEXP)@RequestParam("email")String email, HttpSession session){

        if (authorizeService.sendEmail(email,session.getId(),false)==null){
            return Response.success("邮件已发送,请注意查收");
        }else {
            return Response.failure(400,authorizeService.sendEmail(email,session.getId(),false));
        }
    }

    @PostMapping("/valid-reset-email")
    public Response<Void> validateResetEmail(@Pattern (regexp = EMAIL_REGEXP)@RequestParam("email")String email, HttpSession session){

        if (authorizeService.sendEmail(email,session.getId(),true)==null){
            return Response.success("邮件已发送,请注意查收");
        }else {
            return Response.failure(400,authorizeService.sendEmail(email,session.getId(),true));
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
            return Response.failure(400,authorizeService.validateAndRegister(username,password,email,code,sessionId));
        }
    }

    /**
     * 1.发送验证码并存入redis
     * 2.验证验证码是否正确,正确则在session存入一个标记
     * 3.用户发起密码重置请求,如果存在标记则重置成功
     */
    @PostMapping("/start-reset")
    public Response<String> startReset(
            @Pattern(regexp = EMAIL_REGEXP) String email,
            @Length(min = 6,max = 6) String code,
            HttpSession session
    ){
        String s = authorizeService.validateOnly(email, code, session.getId());
        if (s == null){
            session.setAttribute("reset-password",email);
            return Response.success();
        }else {
            return Response.failure(400,s);
        }
    }

    @PostMapping("/do-reset")
    public Response<String> resetPassword(
            @Pattern(regexp = PASSWORD_REGEXP)@Length(min = 6,max = 16) String password,
            HttpSession session
    ){
        String email = session.getAttribute("reset-password").toString();
        if (email == null){
            return Response.failure(401,"请先完成邮箱验证");
        }else if(authorizeService.resetPassWord(password,email)){
            session.removeAttribute("reset-password");
            return Response.success("密码重置成功");
        }else {
            return Response.failure(500,"内部错误,请联系管理员");
        }
    }
}
