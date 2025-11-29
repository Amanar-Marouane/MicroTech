package com.restapi.microtech.service;

import org.springframework.stereotype.Service;

import com.restapi.microtech.contract.AuthServiceContract;
import com.restapi.microtech.entity.Utilisateur;
import com.restapi.microtech.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceContract {
    private final UtilisateurRepository utilisateurRepository;

    @Override
    public Utilisateur login(String nomUtilisateur, String motDePasse) throws IllegalArgumentException {
        Utilisateur u = utilisateurRepository.findByNomUtilisateur(nomUtilisateur);
        if (u == null) {
            throw new IllegalArgumentException("Credantials not valid");
        }
        if (!u.getMotDePasse().equals(motDePasse)) {
            throw new IllegalArgumentException("Credantials not valid");
        }
        return u;
    }
}
