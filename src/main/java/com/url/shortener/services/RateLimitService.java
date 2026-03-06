package com.url.shortener.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private RedisTemplate<String,String> redisTemplate;
    public RateLimitService(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public Boolean isAllowed(String ip){
        String cacheKey = "rate_limit:ip:" + ip;
        Long count = redisTemplate.opsForValue().increment(cacheKey);

        if (count != null && count == 1){
            redisTemplate.expire(cacheKey, Duration.ofMinutes(1));
        }

        return count != null && count <= 10;
    }
}
