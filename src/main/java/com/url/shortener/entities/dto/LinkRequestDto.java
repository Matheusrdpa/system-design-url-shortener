package com.url.shortener.entities.dto;

import org.jspecify.annotations.Nullable;

public record LinkRequestDto(Long id, String originalUrl) {
}
