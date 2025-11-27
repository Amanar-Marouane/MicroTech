package com.restapi.microtech.dto.response;

import com.restapi.microtech.entity.enums.StatutPaiement;
import com.restapi.microtech.entity.enums.TypePaiement;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class PaiementResponse {

    private Long id;
    private Integer numeroPaiement;
    private BigDecimal montant;
    private TypePaiement typePaiement;
    private Date datePaiement;
    private Date dateEncaissement;
    private StatutPaiement statut;
    private String numeroCheque;
    private Date dateEcheance;
    private String nomBanque;
    private String reference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}