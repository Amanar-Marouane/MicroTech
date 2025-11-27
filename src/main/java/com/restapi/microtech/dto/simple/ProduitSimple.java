package com.restapi.microtech.dto.simple;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProduitSimple {

    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}