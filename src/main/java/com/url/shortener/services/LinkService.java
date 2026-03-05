package com.url.shortener.services;

import com.url.shortener.entities.Link;
import com.url.shortener.entities.dto.LinkRequestDto;
import com.url.shortener.repositories.LinkRepository;
import com.url.shortener.services.Exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class LinkService {
    private final String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }

    public String save(LinkRequestDto linkDto){
        Link link = new Link(null, linkDto.originalUrl(), LocalDateTime.now());
        Link linkWithId = linkRepository.save(link);
        Long id = linkWithId.getId();
        String code = encode(id);
        linkWithId.setCode(code);
        linkRepository.save(linkWithId);
        return "URL Created: " + "http://localhost:8080/" + code;
    }

    public String findByCode(String code){
        Link link = linkRepository.findByCode(code).orElseThrow(() -> new NotFoundException("Url not found for this code"));
        return link.getOriginalUrl();
    }

    public String encode(long i){
        StringBuilder code = new StringBuilder();
        while(i > 0){
            int remainder = (int) i % 62;
            code.append(base62.charAt(remainder));
            i = i / 62;
        }
        return code.reverse().toString();
    }
}
