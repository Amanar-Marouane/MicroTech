package com.restapi.microtech.entity;

import com.restapi.microtech.entity.enums.NiveauClient;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CLIENT")
public class Client extends Utilisateur {

    @Enumerated(EnumType.STRING)
    private NiveauClient niveauFidelite;

    private Integer totalCommandesConfirmees = 0;
    private BigDecimal montantCumule = BigDecimal.ZERO;

    private Date datePremiereCommande;
    private Date dateDerniereCommande;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Commande> commandes;
}