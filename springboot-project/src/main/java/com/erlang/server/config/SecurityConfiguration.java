package com.erlang.server.config;

import com.alibaba.fastjson2.JSONObject;
import com.erlang.server.entity.Response;
import com.erlang.server.service.AuthorizeService;
import com.erlang.server.service.impl.AuthorizeServiceImpl;
import com.erlang.server.utils.MyPersistentTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private MyPersistentTokenRepository repository;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests()
                //放行验证相关请求
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/auth/login")
                .successHandler(this::onAuthenticationSuccess)
                .failureHandler(this::onAuthenticationFailure)
                .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenRepository(repository)
                .userDetailsService(new AuthorizeServiceImpl())
                .and()
                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(this::onAuthenticationSuccess)
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(this::onAuthenticationFailure)
                .and()
                .cors()
                .configurationSource(this.configurationSource())
                .and()
                .build();
    }

    private CorsConfigurationSource configurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("http://localhost:8080");
        cors.setAllowCredentials(true);
        cors.addAllowedMethod("*");
        cors.addAllowedHeader("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",cors);
        return configurationSource;
    }


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and()
                .build();
    }

    //认证失败处理方法
    private void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JSONObject.toJSONString(Response.failure(401,exception.getMessage())));
    }

    //认证成功处理方法
    private void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("UTF-8");
        if (request.getRequestURI().endsWith("/login")){
            response.getWriter().write(JSONObject.toJSONString(Response.success("登陆成功!")));
        }else if (request.getRequestURI().endsWith("/logout")){
            //获取请求头中的数据

            response.getWriter().write(JSONObject.toJSONString(Response.success("退出登陆成功!")));
        }

    }

    //密码加密
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
