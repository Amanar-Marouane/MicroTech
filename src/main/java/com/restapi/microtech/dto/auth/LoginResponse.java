package com.restapi.microtech.dto.auth;

import com.restapi.microtech.entity.enums.UserRole;
import lombok.Data;

@Data
public class LoginResponse {
    
    private Long id;
    private String nomUtilisateur;
    private String nom;
    private String email;
    private UserRole role;
}