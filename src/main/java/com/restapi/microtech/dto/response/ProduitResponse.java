package com.restapi.microtech.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProduitResponse {
    
    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
    private Integer stockDisponible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}