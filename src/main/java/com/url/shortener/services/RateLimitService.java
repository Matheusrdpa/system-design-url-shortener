package com.url.shortener.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    private RedisTemplate<String,String> redisTemplate;
    public RateLimitService(RedisTemplate<String,String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public Boolean isAllowed(String ip) {
        String cacheKey = "rate_limit:ip:" + ip;
        try {
            Long count = redisTemplate.opsForValue().increment(cacheKey);

            if (count != null && count == 1) {
                redisTemplate.expire(cacheKey, Duration.ofHours(1));
            }

            return count != null && count <= 10;
        } catch (Exception e) {
            logger.warn("Redis Unavailable in allowed method", e);
            return false;
        }
    }
}
