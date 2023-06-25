package com.erlang.server.config;

import com.erlang.server.entity.domain.UserVo;
import com.erlang.server.mapper.UserMapper;
import com.erlang.server.utils.DB1RedisCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description
 * @Date 2023/6/23 13:10
 * @Author 二郎
 * 三更灯火五更鸡，
 * 正是男儿读书时。
 * 黑发不知勤学早，
 * 白首方悔读书迟。
 */
@Component
public class AuthorizeInterceptor implements HandlerInterceptor {



    @Autowired
    private DB1RedisCache redisCache;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        UserVo userVo = userMapper.findUserVoByName(user.getUsername());
        request.getSession().setAttribute("account",userVo);
        return true;

    }
}
