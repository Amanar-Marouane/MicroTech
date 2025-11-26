package com.restapi.microtech.dto.request;

import com.restapi.microtech.entity.enums.StatutPaiement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Date;

@Data
public class UpdatePaiementStatutRequest {
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutPaiement statut;
    
    private Date dateEncaissement;
}