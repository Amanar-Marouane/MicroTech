package com.restapi.microtech.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.restapi.microtech.custom.CreatedAt;
import com.restapi.microtech.custom.UpdatedAt;
import com.restapi.microtech.custom.listeners.AuditListener;

@Entity
@EntityListeners(AuditListener.class)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "article_commande")
public class ArticleCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande")
    @NotNull(message = "La commande est obligatoire")
    private Commande commande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit")
    @NotNull(message = "Le produit est obligatoire")
    private Produit produit;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    @NotNull(message = "Le prix unitaire snapshot est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal prixUnitaireSnapshot;

    @NotNull(message = "Le total ligne est obligatoire")
    @DecimalMin(value = "0.0", message = "Le total doit être positif")
    private BigDecimal totalLigne;

    @CreatedAt
    private LocalDateTime createdAt;

    @UpdatedAt
    private LocalDateTime updatedAt;
}