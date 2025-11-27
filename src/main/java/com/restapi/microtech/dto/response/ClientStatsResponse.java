package com.restapi.microtech.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ClientStatsResponse {
    private Long clientId;
    private Integer totalCommandesConfirmees;
    private BigDecimal montantCumule;
    private Date datePremiereCommande;
    private Date dateDerniereCommande;
}
