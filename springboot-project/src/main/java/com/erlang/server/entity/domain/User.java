package com.erlang.server.entity.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String password;
    private String email;
}
