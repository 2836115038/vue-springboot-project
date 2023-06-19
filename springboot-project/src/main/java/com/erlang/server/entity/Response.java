package com.erlang.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Response<T> {

    private Integer code;
    private String message;
    private T data;

    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(){
        return new Response<>(200,null,null);
    }

    public static <T> Response<T> success(String message){
        return new Response<>(200,message,null);
    }

    public static <T> Response<T> success(T data){
        return new Response<>(200,null,data);
    }

    public static <T> Response<T> success(String message,T data){
        return new Response<>(200,message,data);
    }

    public static <T> Response<T> failure(Integer code,String message){
        return new Response<>(code,message,null);
    }
}


