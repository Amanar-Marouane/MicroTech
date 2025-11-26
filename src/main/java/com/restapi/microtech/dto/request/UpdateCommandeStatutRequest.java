package com.restapi.microtech.dto.request;

import com.restapi.microtech.entity.enums.StatutCommande;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCommandeStatutRequest {
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutCommande statut;
}