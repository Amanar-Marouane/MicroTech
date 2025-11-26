package com.restapi.microtech.dto.response;

import com.restapi.microtech.dto.simple.ProduitSimple;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ArticleCommandeResponse {
    
    private Long id;
    private ProduitSimple produit;
    private Integer quantite;
    private BigDecimal prixUnitaireSnapshot;
    private BigDecimal totalLigne;
}