package com.restapi.microtech.entity;

import com.restapi.microtech.entity.enums.StatutPaiement;
import com.restapi.microtech.entity.enums.TypePaiement;
import jakarta.validation.constraints.*;
import jakarta.persistence.Id;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "paiement")
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    @NotNull(message = "La commande est obligatoire")
    private Commande commande;

    @NotNull(message = "Le numéro de paiement est obligatoire")
    @Min(value = 1, message = "Le numéro de paiement doit être positif")
    private Integer numeroPaiement;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant doit être positif")
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de paiement est obligatoire")
    private TypePaiement typePaiement;

    @NotNull(message = "La date de paiement est obligatoire")
    private Date datePaiement;
    private Date dateEncaissement;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut de paiement est obligatoire")
    private StatutPaiement statut;

    private String numeroCheque;
    private Date dateEcheance;
    private String nomBanque;
    private String reference;
}
