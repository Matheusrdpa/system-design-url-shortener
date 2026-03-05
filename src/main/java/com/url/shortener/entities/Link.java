package com.url.shortener.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
public class Link {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String originalUrl;
    private LocalDateTime createdAt;
    @Column(unique = true)
    private String code;

    public Link(Long id, String originalUrl, LocalDateTime createdAt) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
    }

    public Link() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
