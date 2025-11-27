package com.restapi.microtech.controller;

import com.restapi.microtech.contract.OrderServiceContract;
import com.restapi.microtech.dto.request.CommandeRequest;
import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.request.UpdatePaiementStatutRequest;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.PageResponse;
import com.restapi.microtech.dto.response.PaiementResponse;
import com.restapi.microtech.entity.enums.StatutCommande;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping(value = "/commandes", produces = "application/json")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceContract orderService;

    @PostMapping(path = "/clients/{clientId}", consumes = "application/json")
    public ResponseEntity<CommandeResponse> create(@PathVariable Long clientId, @Valid @RequestBody CommandeRequest request) {
        return ResponseEntity.ok(orderService.create(clientId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CommandeResponse>> search(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "clientId", required = false) Long clientId,
            @RequestParam(name = "statut", required = false) StatutCommande statut,
            @RequestParam(name = "dateFrom", required = false) Date dateFrom,
            @RequestParam(name = "dateTo", required = false) Date dateTo,
            @RequestParam(name = "totalMin", required = false) BigDecimal totalMin,
            @RequestParam(name = "totalMax", required = false) BigDecimal totalMax,
            @RequestParam(name = "promoCode", required = false) String promoCode
    ) {
        if (size <= 0) size = 10;
        if (start < 0) start = 0;
        int page = start / size;
        Pageable pageable = PageRequest.of(page, size);
        Page<CommandeResponse> pageResult = orderService.search(clientId, statut, dateFrom, dateTo, totalMin, totalMax, promoCode, pageable);
        PageResponse<CommandeResponse> response = PageResponse.from(pageResult, start, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "{id}/payments", consumes = "application/json")
    public ResponseEntity<PaiementResponse> addPayment(@PathVariable Long id, @Valid @RequestBody PaiementRequest request) {
        return ResponseEntity.ok(orderService.addPayment(id, request));
    }

    @PostMapping(path = "payments/{paiementId}/status", consumes = "application/json")
    public ResponseEntity<PaiementResponse> updatePaymentStatus(@PathVariable Long paiementId, @Valid @RequestBody UpdatePaiementStatutRequest request) {
        return ResponseEntity.ok(orderService.updatePaymentStatus(paiementId, request));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<CommandeResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<CommandeResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
