package com.msauth.exception;

import com.msauth.dto.response.ExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.msauth.enums.ErrorMessage.*;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handle(UserNotFoundException ex, HttpServletRequest request) {
        log.error("NotFoundException : " + ex);
        return ResponseEntity.status(NOT_FOUND)
                .body(ExceptionResponse.builder()
                        .message(ex.getMessage())
                        .statusCode(NOT_FOUND.value())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handle(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.error("UserAlreadyExistsException : " + ex);

        return ResponseEntity.status(CONFLICT)
                .body(ExceptionResponse.builder()
                        .statusCode(CONFLICT.value())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handle(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException : " + ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(BAD_REQUEST)
                .body(ExceptionResponse.builder()
                        .statusCode(BAD_REQUEST.value())
                        .message("Field validation failed")
                        .errors(errors)
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /** SECURITY AND JWT EXCEPTIONS */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ExceptionResponse handle(BadCredentialsException ex, HttpServletRequest request) {
        log.error("BadCredentialsException : " + ex);
        return ExceptionResponse.builder()
                .statusCode(UNAUTHORIZED.value())
                .message(WRONG_PASSWORD_OR_USERNAME.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ExceptionResponse handle(AccessDeniedException ex, HttpServletRequest request) {
        log.error("AccessDeniedException : " + ex);
        return ExceptionResponse.builder()
                .statusCode(FORBIDDEN.value())
                .message(ACCESS_DENIED.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(FORBIDDEN)
    public ExceptionResponse handle(ExpiredJwtException ex, HttpServletRequest request) {
        log.error("ExpiredJwtException : " + ex);
        return ExceptionResponse.builder()
                .statusCode(FORBIDDEN.value())
                .message(EXPIRED_JWT_EXCEPTION.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(SignatureException.class)
    @ResponseStatus(FORBIDDEN)
    public ExceptionResponse handle(SignatureException ex, HttpServletRequest request) {
        log.error("SignatureException : " + ex);
        return ExceptionResponse.builder()
                .statusCode(FORBIDDEN.value())
                .message(INVALID_JWT_SIGNATURE.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(FORBIDDEN)
    public ExceptionResponse handle(MalformedJwtException ex,HttpServletRequest request) {
        log.error("MalformedJwtException : " + ex);
        return ExceptionResponse.builder()
                .statusCode(FORBIDDEN.value())
                .message(MALFORMED_JWT_EXCEPTION.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(FORBIDDEN)
    public ExceptionResponse handle(IllegalArgumentException ex, HttpServletRequest request) {
        log.error("IllegalArgumentException  : " + ex);
        return ExceptionResponse.builder()
                .statusCode(FORBIDDEN.value())
                .message(ILLEGAL_ARGUMENT_EXCEPTION.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}