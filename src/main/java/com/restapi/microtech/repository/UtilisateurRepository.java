package com.restapi.microtech.repository;

import com.restapi.microtech.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Utilisateur findByNomUtilisateur(String nomUtilisateur);
}
