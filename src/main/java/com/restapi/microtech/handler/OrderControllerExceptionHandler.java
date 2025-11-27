package com.restapi.microtech.handler;

import com.restapi.microtech.controller.OrderController;
import com.restapi.microtech.exception.BusinessRuleException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = OrderController.class)
public class OrderControllerExceptionHandler {

    private Map<String, Object> createErrorBody(HttpStatus status, String message, String path) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("path", path);
        return errorBody;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public Map<String, Object> handleBusiness(BusinessRuleException ex, HttpServletRequest request) {
        return createErrorBody(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, Object> errorBody = createErrorBody(HttpStatus.BAD_REQUEST, "Validation failed",
                request.getRequestURI());

        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    Map<String, String> fieldError = new HashMap<>();
                    fieldError.put("field", error.getField());
                    fieldError.put("message", error.getDefaultMessage());
                    return fieldError;
                }).collect(Collectors.toList());

        errorBody.put("errors", fieldErrors);
        return errorBody;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleOtherExceptions(Exception ex, HttpServletRequest request) {
        Map<String, Object> errorBody = createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                request.getRequestURI());
        errorBody.put("details", ex.getMessage());
        return errorBody;
    }
}
