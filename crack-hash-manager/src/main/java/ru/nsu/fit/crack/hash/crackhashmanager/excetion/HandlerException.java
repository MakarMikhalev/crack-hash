package ru.nsu.fit.crack.hash.crackhashmanager.excetion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class HandlerException {
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTaskNotFoundException(TaskNotFoundException taskNotFoundException) {
        ErrorDto errorDto = ErrorDto.builder()
            .createdAt(Instant.now())
            .code(HttpStatus.BAD_REQUEST.value())
            .message(taskNotFoundException.getMessage())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }
}
