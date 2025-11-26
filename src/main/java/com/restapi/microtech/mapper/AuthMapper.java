package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.auth.LoginResponse;
import com.restapi.microtech.dto.auth.RegisterRequest;
import com.restapi.microtech.entity.Admin;
import com.restapi.microtech.entity.Client;
import com.restapi.microtech.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    
    LoginResponse toLoginResponse(Utilisateur utilisateur);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CLIENT")
    @Mapping(target = "niveauFidelite", constant = "BASIC")
    @Mapping(target = "totalCommandesConfirmees", constant = "0")
    @Mapping(target = "montantCumule", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "datePremiereCommande", ignore = true)
    @Mapping(target = "dateDerniereCommande", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    Client toClientEntity(RegisterRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "ADMIN")
    Admin toAdminEntity(RegisterRequest request);
}