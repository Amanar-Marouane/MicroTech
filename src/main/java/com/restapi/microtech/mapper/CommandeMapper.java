package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.request.CommandeRequest;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.simple.CommandeSimple;
import com.restapi.microtech.entity.Client;
import com.restapi.microtech.entity.Commande;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ClientMapper.class, ArticleCommandeMapper.class})
public interface CommandeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", source = "client")
    @Mapping(target = "dateCommande", expression = "java(new java.util.Date())")
    @Mapping(target = "statut", constant = "PENDING")
    @Mapping(target = "sousTotalHT", ignore = true)
    @Mapping(target = "montantRemiseFidelite", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "montantRemisePromo", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "montantRemiseTotal", ignore = true)
    @Mapping(target = "montantHTApresRemise", ignore = true)
    @Mapping(target = "montantTVA", ignore = true)
    @Mapping(target = "totalTTC", ignore = true)
    @Mapping(target = "montantRestant", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "paiements", ignore = true)
    Commande toEntity(CommandeRequest request, Client client);
    
    CommandeResponse toResponse(Commande commande);
    
    CommandeSimple toSimple(Commande commande);
}