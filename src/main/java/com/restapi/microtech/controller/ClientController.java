package com.restapi.microtech.controller;

import com.restapi.microtech.contract.ClientServiceContract;
import com.restapi.microtech.dto.request.ClientRequest;
import com.restapi.microtech.dto.response.ClientResponse;
import com.restapi.microtech.dto.response.ClientStatsResponse;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.LoyaltyTierResponse;
import com.restapi.microtech.entity.Utilisateur;
import com.restapi.microtech.entity.enums.UserRole;
import com.restapi.microtech.exception.ForbiddenException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/clients", produces = "application/json")
@RequiredArgsConstructor
public class ClientController {

    private final ClientServiceContract clientService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ClientResponse> createClient(@RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.createClient(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoints for client self-service views
    @GetMapping("/{id}/profile")
    public ResponseEntity<ClientResponse> getProfile(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return ResponseEntity.status(401).body(null);

        ensureClientAccessAllowed(id, session);

        return ResponseEntity.ok(clientService.getProfile(id));
    }

    @GetMapping("/{id}/loyalty-tier")
    public ResponseEntity<LoyaltyTierResponse> getLoyaltyTier(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return ResponseEntity.status(401).body(null);

        ensureClientAccessAllowed(id, session);

        return ResponseEntity.ok(clientService.getLoyaltyTier(id));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ClientStatsResponse> getStats(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return ResponseEntity.status(401).body(null);

        ensureClientAccessAllowed(id, session);

        return ResponseEntity.ok(clientService.getStatistics(id));
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<List<CommandeResponse>> getOrderHistory(@PathVariable Long id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null)
            return ResponseEntity.status(401).body(null);

        ensureClientAccessAllowed(id, session);

        return ResponseEntity.ok(clientService.getOrderHistory(id));
    }

    private void ensureClientAccessAllowed(Long requestedId, HttpSession session) {
        Utilisateur user = (Utilisateur) session.getAttribute("USER");
        UserRole role = (UserRole) session.getAttribute("ROLE");

        if (role == UserRole.CLIENT) {
            if (!user.getId().equals(requestedId)) {
                throw new ForbiddenException("Forbidden");
            }
        }
    }

}
