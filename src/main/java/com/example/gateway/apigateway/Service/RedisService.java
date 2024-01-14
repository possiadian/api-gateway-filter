package com.example.gateway.apigateway.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RedisService {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public Object fetch(String key){
        return redisTemplate.opsForValue().get(key);
    }
}
