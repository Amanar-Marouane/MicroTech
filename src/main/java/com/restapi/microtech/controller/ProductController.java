package com.restapi.microtech.controller;

import com.restapi.microtech.contract.ProductServiceContract;
import com.restapi.microtech.dto.request.ProduitRequest;
import com.restapi.microtech.dto.response.PageResponse;
import com.restapi.microtech.dto.response.ProduitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/produits", produces = "application/json")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceContract productService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ProduitResponse> create(@Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProduitResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // Single GET endpoint with pagination and filters via specification
    @GetMapping
    public ResponseEntity<PageResponse<ProduitResponse>> search(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "nom", required = false) String nom,
            @RequestParam(name = "prixMin", required = false) BigDecimal prixMin,
            @RequestParam(name = "prixMax", required = false) BigDecimal prixMax,
            @RequestParam(name = "stockMin", required = false) Integer stockMin,
            @RequestParam(name = "stockMax", required = false) Integer stockMax
    ) {
        if (size <= 0) size = 10;
        if (start < 0) start = 0;
        int page = start / size; // interpret start as zero-based offset
        Pageable pageable = PageRequest.of(page, size);
        Page<ProduitResponse> pageResult = productService.search(nom, prixMin, prixMax, stockMin, stockMax, pageable);
        PageResponse<ProduitResponse> response = PageResponse.from(pageResult, start, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<ProduitResponse> update(@PathVariable Long id, @Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
