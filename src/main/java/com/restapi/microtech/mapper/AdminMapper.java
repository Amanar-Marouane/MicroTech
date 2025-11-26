package com.restapi.microtech.mapper;

import com.restapi.microtech.dto.auth.RegisterRequest;
import com.restapi.microtech.dto.simple.AdminSimple;
import com.restapi.microtech.entity.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", constant = "ADMIN")
    Admin toEntity(RegisterRequest request);
    
    AdminSimple toSimple(Admin admin);
}