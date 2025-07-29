package com.huyle.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleValidationException(Exception ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        switch (ex) {
            case MethodArgumentNotValidException validationEx -> {
                for (FieldError fieldError : validationEx.getBindingResult().getFieldErrors()) {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
            }
            case HttpMessageNotReadableException notReadableEx -> {
                Throwable cause = notReadableEx.getMostSpecificCause();
                if (cause instanceof IllegalArgumentException illegalArgEx) {
                    errors.put("role", illegalArgEx.getMessage()); // Enum parsing error
                } else {
                    errors.put("request", "Invalid request format");
                }
            }
            default -> {
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getDescription(false).substring(4));
        response.put("message", "Validation error");
        response.put("errors", errors);

        log.warn("Validation error: {}", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("path", request.getDescription(false).substring(4));
        response.put("message", ex.getMessage());

        log.error("Unhandled exception occurred", ex);

        return ResponseEntity.badRequest().body(response);
    }
}
