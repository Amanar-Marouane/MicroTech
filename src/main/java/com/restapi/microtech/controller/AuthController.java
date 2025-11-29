package com.restapi.microtech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restapi.microtech.contract.AuthServiceContract;
import com.restapi.microtech.dto.auth.LoginRequest;
import com.restapi.microtech.dto.auth.LoginResponse;
import com.restapi.microtech.entity.Utilisateur;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth", produces = "application/json")
public class AuthController {
    private final AuthServiceContract authService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(HttpServletRequest request,
            @RequestBody LoginRequest data) {
        Utilisateur u = authService.login(data.getNomUtilisateur(), data.getMotDePasse());
        LoginResponse response = new LoginResponse();
        response.setId(u.getId());
        response.setNomUtilisateur(u.getNomUtilisateur());
        response.setNom(u.getNom());
        response.setEmail(u.getEmail());
        response.setRole(u.getRole());

        HttpSession session = request.getSession(true);
        session.setAttribute("USER", u);
        session.setAttribute("ROLE", u.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null)
            session.invalidate();

        return ResponseEntity.ok("Logout with success");
    }

}
