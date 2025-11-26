package com.restapi.microtech.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CommandeRequest {
    
    @NotEmpty(message = "La commande doit contenir au moins un article")
    @Valid
    private List<ArticleCommandeRequest> articles;
    
    private String codePromo;
}