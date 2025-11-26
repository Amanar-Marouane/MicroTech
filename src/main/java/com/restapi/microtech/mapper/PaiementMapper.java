package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.response.PaiementResponse;
import com.restapi.microtech.entity.Commande;
import com.restapi.microtech.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaiementMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commande", source = "commande")
    @Mapping(target = "numeroPaiement", ignore = true)
    @Mapping(target = "datePaiement", expression = "java(new java.util.Date())")
    @Mapping(target = "dateEncaissement", ignore = true)
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    Paiement toEntity(PaiementRequest request, Commande commande);
    
    PaiementResponse toResponse(Paiement paiement);
}