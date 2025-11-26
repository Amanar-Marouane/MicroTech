package com.restapi.microtech.entity;

import com.restapi.microtech.entity.enums.StatutCommande;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "commande")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    @NotNull(message = "Le client est obligatoire")
    private Client client;

    @NotNull(message = "La date de commande est obligatoire")
    private Date dateCommande;

    @NotNull(message = "Le sous-total HT est obligatoire")
    @DecimalMin(value = "0.0", message = "Le sous-total doit être positif")
    private BigDecimal sousTotalHT;
    @Builder.Default
    private BigDecimal montantRemiseFidelite = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal montantRemisePromo = BigDecimal.ZERO;
    private BigDecimal montantRemiseTotal;
    private BigDecimal montantHTApresRemise;
    private BigDecimal montantTVA;
    private BigDecimal totalTTC;

    private String codePromo;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut est obligatoire")
    private StatutCommande statut;

    private BigDecimal montantRestant;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<ArticleCommande> articles;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Paiement> paiements;
}