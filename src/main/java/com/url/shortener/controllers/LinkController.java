package com.url.shortener.controllers;


import com.url.shortener.entities.dto.LinkRequestDto;
import com.url.shortener.services.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/")
public class LinkController {
    private LinkService linkService;
    public LinkController(LinkService linkService){
        this.linkService = linkService;
    }

    @PostMapping("/url")
    public ResponseEntity<String> saveLink(@RequestBody LinkRequestDto link){
        String url = linkService.save(link);
        return ResponseEntity.created(URI.create(url)).body(url);
    }

    @GetMapping("/{code}")
    public ResponseEntity<String> redirectLink(@PathVariable String code){
        String res = linkService.findByCode(code);
        return ResponseEntity.status(302).location(URI.create(res)).build();
    }
}
