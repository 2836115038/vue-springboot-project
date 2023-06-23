package com.erlang.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description
 * @Date 2023/6/23 13:12
 * @Author 二郎
 * 三更灯火五更鸡，
 * 正是男儿读书时。
 * 黑发不知勤学早，
 * 白首方悔读书迟。
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthorizeInterceptor authorizeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizeInterceptor)
                .addPathPatterns("/**");
    }
}
