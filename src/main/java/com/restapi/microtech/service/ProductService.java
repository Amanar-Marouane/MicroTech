package com.restapi.microtech.service;

import com.restapi.microtech.contract.ProductServiceContract;
import com.restapi.microtech.dto.request.ProduitRequest;
import com.restapi.microtech.dto.response.ProduitResponse;
import com.restapi.microtech.entity.Produit;
import com.restapi.microtech.mapper.ProduitMapper;
import com.restapi.microtech.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceContract {

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    @Override
    public ProduitResponse create(ProduitRequest request) {
        Produit entity = produitMapper.toEntity(request);
        entity = produitRepository.save(entity);
        return produitMapper.toResponse(entity);
    }

    @Override
    public ProduitResponse getById(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit not found with id: " + id));
        return produitMapper.toResponse(produit);
    }

    @Override
    public ProduitResponse update(Long id, ProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit not found with id: " + id));
        produitMapper.updateEntity(request, produit);
        produit = produitRepository.save(produit);
        return produitMapper.toResponse(produit);
    }

    @Override
    public void delete(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new EntityNotFoundException("Produit not found with id: " + id);
        }
        produitRepository.deleteById(id);
    }

    @Override
    public Page<ProduitResponse> search(String nom,
                                        BigDecimal prixMin,
                                        BigDecimal prixMax,
                                        Integer stockMin,
                                        Integer stockMax,
                                        Pageable pageable) {
        Specification<Produit> specification = buildSpecification(nom, prixMin, prixMax, stockMin, stockMax);
        return produitRepository.findAll(specification, pageable).map(produitMapper::toResponse);
    }

    private Specification<Produit> buildSpecification(String nom,
                                                       BigDecimal prixMin,
                                                       BigDecimal prixMax,
                                                       Integer stockMin,
                                                       Integer stockMax) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (nom != null && !nom.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("nom")), "%" + nom.toLowerCase() + "%"));
            }
            if (prixMin != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("prixUnitaire"), prixMin));
            }
            if (prixMax != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("prixUnitaire"), prixMax));
            }
            if (stockMin != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("stockDisponible"), stockMin));
            }
            if (stockMax != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("stockDisponible"), stockMax));
            }
            return predicate;
        };
    }
}
