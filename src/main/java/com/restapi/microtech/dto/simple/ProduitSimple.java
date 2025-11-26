package com.restapi.microtech.dto.simple;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitSimple {
    
    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
}