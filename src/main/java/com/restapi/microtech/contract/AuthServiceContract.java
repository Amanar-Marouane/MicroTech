package com.restapi.microtech.contract;

import com.restapi.microtech.entity.Utilisateur;

public interface AuthServiceContract {
    Utilisateur login(String nomUtilisateur, String motDePasse) throws IllegalArgumentException;
}
