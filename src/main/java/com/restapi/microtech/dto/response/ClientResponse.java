package com.restapi.microtech.dto.response;

import com.restapi.microtech.entity.enums.NiveauClient;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ClientResponse {
    
    private Long id;
    private String nomUtilisateur;
    private String nom;
    private String email;
    private NiveauClient niveauFidelite;
    private Integer totalCommandesConfirmees;
    private BigDecimal montantCumule;
    private Date datePremiereCommande;
    private Date dateDerniereCommande;
}