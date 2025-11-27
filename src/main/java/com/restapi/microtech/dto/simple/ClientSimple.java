package com.restapi.microtech.dto.simple;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClientSimple {

    private Long id;
    private String nom;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}