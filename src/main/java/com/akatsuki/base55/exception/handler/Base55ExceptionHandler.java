package com.akatsuki.base55.exception.handler;

import com.akatsuki.base55.exception.Base55Exception;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Base55ExceptionHandler {

    @ExceptionHandler(TransientAiException.class)
    public ResponseEntity<String> handleTransientAiException(TransientAiException e) {
        // Check if this is a 429 rate-limit error
        if (e.getMessage() != null && e.getMessage().contains("HTTP 429")) {
            // Return only the exception message, no stack trace
            return ResponseEntity.status(429).body(e.getMessage());
        }
        // Fallback for other AI exceptions
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(Base55Exception.class)
    public ResponseEntity<String> handleBase55Exception(Base55Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
