package com.restapi.microtech.entity;

import java.time.LocalDateTime;

import com.restapi.microtech.custom.CreatedAt;
import com.restapi.microtech.custom.UpdatedAt;
import com.restapi.microtech.custom.listeners.AuditListener;
import com.restapi.microtech.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@EntityListeners(AuditListener.class)
@Getter
@Setter
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(unique = true)
    private String nomUtilisateur;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @CreatedAt
    private LocalDateTime createdAt;

    @UpdatedAt
    private LocalDateTime updatedAt;

}