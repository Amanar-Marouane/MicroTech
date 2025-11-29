package com.restapi.microtech.handler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.restapi.microtech.controller.AuthController;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthControllerExceptionHandler {

    private Map<String, Object> createErrorBody(HttpStatus status, String message, String path) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("path", path);
        return errorBody;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleValidationExceptions(IllegalArgumentException ex,
            HttpServletRequest request) {
        Map<String, Object> errorBody = createErrorBody(HttpStatus.UNAUTHORIZED, "Auth Failed",
                request.getRequestURI());

        errorBody.put("errors", ex.getMessage());
        return errorBody;
    }
}
