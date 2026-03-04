package com.url.shortener.services;

import com.url.shortener.entities.Link;
import com.url.shortener.entities.dto.LinkRequestDto;
import com.url.shortener.repositories.LinkRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    private LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }

    public LinkRequestDto save(LinkRequestDto linkDto){
        Link link = new Link(null, linkDto.originalUrl(), LocalDateTime.now());
        linkRepository.save(link);
        return linkDto;
    }

    public List<Link> findAll(){
        return linkRepository.findAll();
    }

    public Link update(Long id, Link link){
        Link newLink = linkRepository.getReferenceById(id);
        newLink.setCreatedAt(link.getCreatedAt());
        newLink.setOriginalUrl(link.getOriginalUrl());
        return linkRepository.save(newLink);
    }

    public void delete(Long id){
        linkRepository.deleteById(id);
    }

    public Optional<Link> findById(Long id){
        return linkRepository.findById(id);
    }
}
