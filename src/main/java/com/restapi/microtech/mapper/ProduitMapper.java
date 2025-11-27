package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.request.ProduitRequest;
import com.restapi.microtech.dto.response.ProduitResponse;
import com.restapi.microtech.dto.simple.ProduitSimple;
import com.restapi.microtech.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Produit toEntity(ProduitRequest request);

    ProduitResponse toResponse(Produit produit);

    ProduitSimple toSimple(Produit produit);

    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(ProduitRequest request, @MappingTarget Produit produit);
}