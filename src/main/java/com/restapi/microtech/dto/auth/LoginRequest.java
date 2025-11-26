package com.restapi.microtech.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String nomUtilisateur;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}