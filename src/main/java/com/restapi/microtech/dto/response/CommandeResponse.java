package com.restapi.microtech.dto.response;

import com.restapi.microtech.dto.simple.ClientSimple;
import com.restapi.microtech.entity.enums.StatutCommande;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class CommandeResponse {

    private Long id;
    private ClientSimple client;
    private Date dateCommande;
    private BigDecimal sousTotalHT;
    private BigDecimal montantRemiseFidelite;
    private BigDecimal montantRemisePromo;
    private BigDecimal montantRemiseTotal;
    private BigDecimal montantHTApresRemise;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;
    private String codePromo;
    private StatutCommande statut;
    private BigDecimal montantRestant;
    private List<ArticleCommandeResponse> articles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}