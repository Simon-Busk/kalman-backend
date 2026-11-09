package com.kalman.controller.handler;

import com.kalman.service.exceptions.InvalidMeasurementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    //Custom exception handler for validation.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handle(MethodArgumentNotValidException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errors.put(f.getField(), f.getDefaultMessage()));
        e.getBindingResult().getGlobalErrors().forEach(f -> errors.put(f.getObjectName(), f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidMeasurementException.class)
    public ResponseEntity<?> handle(InvalidMeasurementException e) {
        return ResponseEntity.badRequest().body(Map.of("measurement", e.getMessage()));
    }
}
