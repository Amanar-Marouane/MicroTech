package com.restapi.microtech.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
@Table(name = "produit")
@SQLDelete(sql = "UPDATE produit SET supprime_a = now() WHERE id = ?")
@SQLRestriction("supprime_a is NULL")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal prixUnitaire;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stockDisponible;

    @CreatedAt
    private LocalDateTime createdAt;

    @UpdatedAt
    private LocalDateTime updatedAt;

    @Column(name = "supprime_a")
    private LocalDateTime deletedAt;
}
