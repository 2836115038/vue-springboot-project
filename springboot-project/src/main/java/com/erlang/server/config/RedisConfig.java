package com.erlang.server.config;


import com.erlang.server.utils.FastJsonRedisSerializer;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(RedisConfig.RedisProperties.class)
public class RedisConfig {

    public final RedisProperties redisProperties;

    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }


    //构建template
    @Bean(name = "database1")
    public RedisTemplate<String, Object> redisTemplate1() {
        return db1CreateRedisTemplate();
    }

    @Bean(name = "database2")
    public RedisTemplate<String, Object> redisTemplate2() {
        return db2CreateRedisTemplate();
    }


    //序列化配置
    private RedisTemplate<String, Object> db1CreateRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(DB1ConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();

        return template;
    }
    private RedisTemplate<String, Object> db2CreateRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(DB2ConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new FastJsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new FastJsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();

        return template;
    }

    @ConfigurationProperties(prefix = "spring.data.redis")
    @Data
    public static class RedisProperties {
        private String host;
        private int port;
        private String password;
        private Integer database1;
        private Integer database2;

    }

    //连接池配置
    private RedisConnectionFactory DB1ConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setPort(redisProperties.getPort());
        configuration.setHostName(redisProperties.getHost());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(1);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }
    private RedisConnectionFactory DB2ConnectionFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setPort(redisProperties.getPort());
        configuration.setHostName(redisProperties.getHost());
        configuration.setPassword(redisProperties.getPassword());
        configuration.setDatabase(2);
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }
}