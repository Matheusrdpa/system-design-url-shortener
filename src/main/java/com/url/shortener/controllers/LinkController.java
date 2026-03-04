package com.url.shortener.controllers;

import com.url.shortener.entities.Link;
import com.url.shortener.entities.dto.LinkRequestDto;
import com.url.shortener.services.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class LinkController {
    private LinkService linkService;
    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    @PostMapping("/url")
    public ResponseEntity<LinkRequestDto> saveLink(@RequestBody LinkRequestDto link){
        LinkRequestDto res = linkService.save(link);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(res.id()).toUri();
        return ResponseEntity.created(uri).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> redirectLink(@PathVariable Long id){
        Optional<Link> link = linkService.findById(id);
        String url = link.get().getOriginalUrl();
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }
}
