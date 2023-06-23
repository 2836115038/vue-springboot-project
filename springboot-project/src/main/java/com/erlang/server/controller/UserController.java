package com.erlang.server.controller;

import com.erlang.server.entity.Response;
import com.erlang.server.entity.domain.UserVo;
import org.springframework.web.bind.annotation.*;

/**
 * @Description
 * @Date 2023/6/23 13:06
 * @Author 二郎
 * 三更灯火五更鸡，
 * 正是男儿读书时。
 * 黑发不知勤学早，
 * 白首方悔读书迟。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public Response<UserVo> me(@SessionAttribute("account") UserVo user){
        return Response.success(user);

    }
}
