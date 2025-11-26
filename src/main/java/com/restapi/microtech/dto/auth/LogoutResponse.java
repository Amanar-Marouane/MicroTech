package com.restapi.microtech.dto.auth;

import lombok.Data;

@Data
public class LogoutResponse {
    
    private String message;
    private boolean success;
}