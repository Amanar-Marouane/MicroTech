package com.restapi.microtech.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitResponse {
    
    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
    private Integer stockDisponible;
    private Boolean estSupprime;
}