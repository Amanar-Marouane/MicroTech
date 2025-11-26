package com.restapi.microtech.dto.request;

import com.restapi.microtech.entity.enums.TypePaiement;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PaiementRequest {
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;
    
    @NotNull(message = "Le type de paiement est obligatoire")
    private TypePaiement typePaiement;
    
    private String numeroCheque;
    private Date dateEcheance;
    private String nomBanque;
    private String reference;
}