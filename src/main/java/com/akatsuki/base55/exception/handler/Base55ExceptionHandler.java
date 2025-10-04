package com.akatsuki.base55.exception.handler;

import com.akatsuki.base55.exception.AgentNotFound;
import com.akatsuki.base55.exception.Base55Exception;
import com.akatsuki.base55.exception.ToolNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@Slf4j
public class Base55ExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleTooManyRequests(HttpClientErrorException.TooManyRequests e) {
        log.warn("Rate limit exceeded. Request failed with HTTP 429: {}", e.getMessage());
    }

    @ExceptionHandler(Base55Exception.class)
    public ResponseEntity<String> handleBase55Exception(Base55Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(ToolNotFoundException.class)
    public ResponseEntity<String> handleToolNotFoundException(ToolNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AgentNotFound.class)
    public ResponseEntity<String> handleAgentNotFoundException(AgentNotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
