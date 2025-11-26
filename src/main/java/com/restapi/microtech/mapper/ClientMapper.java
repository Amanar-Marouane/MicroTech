package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.request.ClientRequest;
import com.restapi.microtech.dto.response.ClientResponse;
import com.restapi.microtech.dto.simple.ClientSimple;
import com.restapi.microtech.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "CLIENT")
    @Mapping(target = "totalCommandesConfirmees", constant = "0")
    @Mapping(target = "montantCumule", expression = "java(java.math.BigDecimal.ZERO)")
    @Mapping(target = "datePremiereCommande", ignore = true)
    @Mapping(target = "dateDerniereCommande", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    Client toEntity(ClientRequest request);
    
    ClientResponse toResponse(Client client);
    
    ClientSimple toSimple(Client client);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "totalCommandesConfirmees", ignore = true)
    @Mapping(target = "montantCumule", ignore = true)
    @Mapping(target = "datePremiereCommande", ignore = true)
    @Mapping(target = "dateDerniereCommande", ignore = true)
    @Mapping(target = "commandes", ignore = true)
    void updateEntity(ClientRequest request, @MappingTarget Client client);
}