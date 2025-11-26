package com.restapi.microtech.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatistiquesResponse {
    
    private Long totalClients;
    private Long totalCommandes;
    private Long commandesConfirmees;
    private Long commandesEnAttente;
    private BigDecimal chiffreAffaires;
    private BigDecimal montantEnAttentePaiement;
}