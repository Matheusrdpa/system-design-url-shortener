package com.url.shortener.services.Exception;

import com.url.shortener.services.Exception.ExceptionDto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> notFound(NotFoundException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorDto dto = new ErrorDto(LocalDateTime.now(), status.value(),ex.getMessage());
        return ResponseEntity.status(status).body(dto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> badRequest(IllegalArgumentException ex){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorDto dto = new ErrorDto(LocalDateTime.now(), status.value(),ex.getMessage());
        return ResponseEntity.status(status).body(dto);
    }
}
