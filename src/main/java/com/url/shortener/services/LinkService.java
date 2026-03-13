package com.url.shortener.services;

import com.url.shortener.entities.Link;
import com.url.shortener.entities.dto.LinkRequestDto;
import com.url.shortener.repositories.LinkRepository;
import com.url.shortener.services.Exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class LinkService {
    @Value("${awsURL}")
    private String awsUrl;
    private static final Logger logger = LoggerFactory.getLogger(LinkService.class);
    private RedisTemplate<String,String> redisTemplate;
    private final String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository,RedisTemplate<String,String> redisTemplate){
        this.linkRepository = linkRepository;
        this.redisTemplate = redisTemplate;
    }

    public String save(LinkRequestDto linkDto){
        logger.info("creating short link originalUrl={}", linkDto.originalUrl());
        Link verifiedLink = linkRepository.findByOriginalUrl(linkDto.originalUrl());
        Link link = new Link(null, linkDto.originalUrl(), LocalDateTime.now());
        if (verifiedLink == null){
            String url = validateAndNormalizeUrl(link.getOriginalUrl());
            link.setOriginalUrl(url);
            Link linkWithId = linkRepository.save(link);
            Long id = linkWithId.getId();
            String code = encode(id);
            linkWithId.setCode(code);
            Link saved = linkRepository.save(linkWithId);
            logger.info("short link created code={} originalUrl={}",saved.getCode(),saved.getOriginalUrl());
            return awsUrl + code;
        }else {
            logger.info("Link retrieved {} originalUrl={}", verifiedLink.getCode(),verifiedLink.getOriginalUrl());
            return awsUrl + verifiedLink.getCode();
        }
    }

    public String findByCode(String code){
        logger.info("Searching for code");
        String cacheKey = "short:url:" + code;
        try{
            String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

            if (cachedUrl != null){
                logger.info("Cache hit");
                return cachedUrl;
            }
        }catch (Exception e){
            logger.warn("Url not cached,searching on database");
        }

        Link link = linkRepository.findByCode(code).orElseThrow(() -> new NotFoundException("Url not found for this code"));
        String originalUrl = link.getOriginalUrl();

        try{
            redisTemplate.opsForValue().set(cacheKey,originalUrl, Duration.ofHours(1));
        }catch (Exception e){
            logger.warn("Redis unavailable");
        }

        logger.info("Cache miss");
        return originalUrl;
    }

    public String encode(long i){
        logger.info("Enconding id into base62");
        StringBuilder code = new StringBuilder();
        while(i > 0){
            int remainder = (int) i % 62;
            code.append(base62.charAt(remainder));
            i = i / 62;
        }
        logger.info("finished enconding");
        return code.reverse().toString();
    }

    public String validateAndNormalizeUrl(String url){
        if (!url.startsWith("http://") && !url.startsWith("https://")){
            url = "https://" + url;
            logger.info("added https in url");
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")){
                throw new IllegalArgumentException("Invalid protocol");
            }
            String host = uri.getHost();
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("Invalid host");
            }
            logger.info("url normalized");
            return url;

        }catch (URISyntaxException e){
            throw new IllegalArgumentException("Invalid url");
        }
    }
}
