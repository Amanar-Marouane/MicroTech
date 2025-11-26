package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.request.ArticleCommandeRequest;
import com.restapi.microtech.dto.response.ArticleCommandeResponse;
import com.restapi.microtech.entity.ArticleCommande;
import com.restapi.microtech.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProduitMapper.class})
public interface ArticleCommandeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", ignore = true)
    @Mapping(target = "produit", source = "produit")
    @Mapping(target = "prixUnitaireSnapshot", source = "produit.prixUnitaire")
    @Mapping(target = "totalLigne", expression = "java(calculateTotalLigne(request.getQuantite(), produit.getPrixUnitaire()))")
    ArticleCommande toEntity(ArticleCommandeRequest request, Produit produit);
    
    ArticleCommandeResponse toResponse(ArticleCommande articleCommande);
    
    @Named("calculateTotalLigne")
    default java.math.BigDecimal calculateTotalLigne(Integer quantite, java.math.BigDecimal prixUnitaire) {
        if (quantite == null || prixUnitaire == null) {
            return java.math.BigDecimal.ZERO;
        }
        return prixUnitaire.multiply(java.math.BigDecimal.valueOf(quantite));
    }
}