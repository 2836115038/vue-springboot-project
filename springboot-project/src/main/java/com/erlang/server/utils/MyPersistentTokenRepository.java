package com.erlang.server.utils;

import com.erlang.server.entity.domain.User;
import com.erlang.server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class MyPersistentTokenRepository implements PersistentTokenRepository {

    @Autowired
    private DB1RedisCache redisCache;

    //令牌过期时间
    private final static long TOKEN_VALID_DAYS=14;

    //创建令牌--参数传入对应的信息
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        //token包括username,series, tokenValue,date 4个属性;
        //生成一个存储Token信息的Key
        String key = generateKey(token.getSeries());
        //生成一个存储series的key,因为下面removeToken传入的参数为username,所以用username生成一个key来获取唯一的series
        //先存储usernamekey
        String usernamekey=generateKey(token.getUsername());
        redisCache.set(usernamekey,token.getSeries());
        redisCache.expire(usernamekey,TOKEN_VALID_DAYS, TimeUnit.DAYS);

        //创建一个hashmap
        Map<String,Object> map=new HashMap<>();
        map.put("username",token.getUsername());
        map.put("tokenValue",token.getTokenValue());
        map.put("date",String.valueOf(token.getDate().getTime()));
        map.put("series",token.getSeries());
        //将Token数据存入redis
        redisCache.hPutAll(key,map);
        redisCache.expire(key,TOKEN_VALID_DAYS, TimeUnit.DAYS);
    }

    //更新令牌
    @Override
    public void updateToken(String series, String tokenValue, Date date) {
        String key = generateKey(series);
        if (redisCache.hasKey(key))
        {
            redisCache.hPut(key,"tokenValue",tokenValue);
            redisCache.hPut(key,"date",String.valueOf(date.getTime()));
        }

    }

    //获取Token通过series
    @Override
    public PersistentRememberMeToken getTokenForSeries(String series) {

        String key = generateKey(series);
        //创建一个ArrayList用来获取多个value
        List<Object> hashKeys = new ArrayList<>();
        hashKeys.add("username");
        hashKeys.add("tokenValue");
        hashKeys.add("date");
        List<Object> hashValues = redisCache.hMultiGet(key, hashKeys);


        String username =  hashValues.get(0).toString();
        String tokenValue = hashValues.get(1).toString();
        String date = hashValues.get(2).toString();

        if (null == username || null == tokenValue || null == date) {
            return null;
        }
        Long timestamp = Long.valueOf(date);
        Date time = new Date(timestamp);

        return new PersistentRememberMeToken(username, series, tokenValue, time);
    }

    //移除对应的Token
    @Override
    public void removeUserTokens(String username) {
        //因为传入的是username,所以我们先用刚才创建的usernamekey获取到series的值
        String Usernamekey=generateKey(username);
        Object o = redisCache.get(Usernamekey);
        
        String key=generateKey(String.valueOf(o));
        if (o!=null){
            redisCache.delete(Usernamekey);
            redisCache.delete(key);
        }

    }
    //生成对应的唯一key
    private String generateKey(String series) {
        return "User:token:" +series ;
    }
}
