package com.restapi.microtech.service;

import com.restapi.microtech.contract.OrderServiceContract;
import com.restapi.microtech.contract.OrderItemServiceContract;
import com.restapi.microtech.contract.PaymentDomainServiceContract;
import com.restapi.microtech.contract.PromoServiceContract;
import com.restapi.microtech.dto.request.CommandeRequest;
import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.request.UpdatePaiementStatutRequest;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.PaiementResponse;
import com.restapi.microtech.entity.*;
import com.restapi.microtech.entity.enums.*;
import com.restapi.microtech.exception.BusinessRuleException;
import com.restapi.microtech.mapper.CommandeMapper;
import com.restapi.microtech.mapper.PaiementMapper;
import com.restapi.microtech.repository.*;
import com.restapi.microtech.util.MoneyUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceContract {

    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;

    private final CommandeMapper commandeMapper;
    private final PaiementMapper paiementMapper;

    // Extracted domain services
    private final PromoServiceContract promoService;
    private final OrderItemServiceContract orderItemService;
    private final PaymentDomainServiceContract paymentDomainService;

    @Value("${app.tva.rate:0.20}")
    private BigDecimal tvaRate;

    // promo pattern moved to PromoService

    @Override
    @Transactional
    public CommandeResponse create(Long clientId, CommandeRequest request) {
        if (request == null || request.getArticles() == null || request.getArticles().isEmpty()) {
            throw new BusinessRuleException("La commande doit contenir au moins un article");
        }

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client introuvable: " + clientId));

        Commande commande = commandeMapper.toEntity(request, client);
        if (commande.getArticles() == null) {
            commande.setArticles(new ArrayList<>());
        }

        // Build articles via dedicated service
        OrderItemServiceContract.BuildResult buildResult = orderItemService.buildArticles(commande, request.getArticles());
        commande.getArticles().addAll(buildResult.getArticles());
        boolean stockInsuffisant = buildResult.isStockInsuffisant();
        BigDecimal sousTotal = buildResult.getSousTotal();
        commande.setSousTotalHT(sousTotal);

        // Remises
        BigDecimal remiseFidelite = calculateLoyaltyDiscount(client.getNiveauFidelite(), sousTotal);
        BigDecimal remisePromo = promoService.computePromoDiscount(request.getCodePromo(), sousTotal);
        if (request.getCodePromo() != null && !request.getCodePromo().isBlank()) {
            // if code was provided and valid, computePromoDiscount would not throw; set code on order
            if (remisePromo.compareTo(BigDecimal.ZERO) > 0) {
                commande.setCodePromo(request.getCodePromo());
            }
        }
        commande.setMontantRemiseFidelite(remiseFidelite);
        commande.setMontantRemisePromo(remisePromo);
        BigDecimal remiseTotale = MoneyUtil.twoDecimals(remiseFidelite.add(remisePromo));
        commande.setMontantRemiseTotal(remiseTotale);

        BigDecimal htApresRemise = MoneyUtil.twoDecimals(sousTotal.subtract(remiseTotale));
        if (htApresRemise.compareTo(BigDecimal.ZERO) < 0) htApresRemise = BigDecimal.ZERO;
        commande.setMontantHTApresRemise(htApresRemise);

        BigDecimal tva = MoneyUtil.twoDecimals(htApresRemise.multiply(Objects.requireNonNullElse(tvaRate, new BigDecimal("0.20"))));
        commande.setMontantTVA(tva);

        BigDecimal totalTTC = MoneyUtil.twoDecimals(htApresRemise.add(tva));
        commande.setTotalTTC(totalTTC);

        commande.setMontantRestant(totalTTC);
        commande.setStatut(stockInsuffisant ? StatutCommande.REJECTED : StatutCommande.PENDING);

        commande = commandeRepository.save(commande);
        return commandeMapper.toResponse(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeResponse getById(Long id) {
        Commande cmd = commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable: " + id));
        return commandeMapper.toResponse(cmd);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommandeResponse> search(Long clientId, StatutCommande statut, Date dateFrom, Date dateTo, BigDecimal totalMin, BigDecimal totalMax, String promoCode, Pageable pageable) {
        Specification<Commande> spec = buildSpec(clientId, statut, dateFrom, dateTo, totalMin, totalMax, promoCode);
        return commandeRepository.findAll(spec, pageable).map(commandeMapper::toResponse);
    }

    @Override
    @Transactional
    public PaiementResponse addPayment(Long commandeId, PaiementRequest request) {
        Commande cmd = commandeRepository.findById(commandeId)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable: " + commandeId));
        if (isFinal(cmd.getStatut())) {
            throw new BusinessRuleException("Impossible d'ajouter un paiement à une commande finale");
        }
        Paiement paiement = paymentDomainService.addPaymentForOrder(cmd, request);
        return paiementMapper.toResponse(paiement);
    }

    @Override
    @Transactional
    public PaiementResponse updatePaymentStatus(Long paiementId, UpdatePaiementStatutRequest request) {
        Paiement p = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new EntityNotFoundException("Paiement introuvable: " + paiementId));
        if (isFinal(p.getCommande().getStatut())) {
            throw new BusinessRuleException("Impossible de modifier un paiement d'une commande finale");
        }
        Paiement saved = paymentDomainService.updatePaymentStatus(p, request);
        return paiementMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CommandeResponse confirm(Long id) {
        Commande cmd = commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable: " + id));
        if (cmd.getStatut() != StatutCommande.PENDING) {
            throw new BusinessRuleException("Seules les commandes en attente peuvent être confirmées");
        }
        if (cmd.getMontantRestant() == null || cmd.getMontantRestant().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessRuleException("La commande doit être totalement payée avant confirmation");
        }

        // Décrémenter le stock produits
        for (ArticleCommande ac : cmd.getArticles()) {
            Produit produit = ac.getProduit();
            int nouveauStock = produit.getStockDisponible() - ac.getQuantite();
            if (nouveauStock < 0) {
                throw new BusinessRuleException("Stock insuffisant lors de la confirmation pour le produit: " + produit.getNom());
            }
            produit.setStockDisponible(nouveauStock);
            produitRepository.save(produit);
        }

        // MAJ stats client et niveau
        Client client = cmd.getClient();
        client.setTotalCommandesConfirmees(client.getTotalCommandesConfirmees() + 1);
        BigDecimal totalCumule = client.getMontantCumule() == null ? BigDecimal.ZERO : client.getMontantCumule();
        client.setMontantCumule(MoneyUtil.twoDecimals(totalCumule.add(cmd.getTotalTTC())));
        Date now = new Date();
        if (client.getDatePremiereCommande() == null) client.setDatePremiereCommande(now);
        client.setDateDerniereCommande(now);
        client.setNiveauFidelite(recalculateTier(client));
        clientRepository.save(client);

        cmd.setStatut(StatutCommande.CONFIRMED);
        commandeRepository.save(cmd);
        return commandeMapper.toResponse(cmd);
    }

    @Override
    @Transactional
    public CommandeResponse cancel(Long id) {
        Commande cmd = commandeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commande introuvable: " + id));
        if (cmd.getStatut() != StatutCommande.PENDING) {
            throw new BusinessRuleException("Seules les commandes en attente peuvent être annulées");
        }
        cmd.setStatut(StatutCommande.CANCELED);
        commandeRepository.save(cmd);
        return commandeMapper.toResponse(cmd);
    }

    private Specification<Commande> buildSpec(Long clientId,
                                              StatutCommande statut,
                                              Date dateFrom,
                                              Date dateTo,
                                              BigDecimal totalMin,
                                              BigDecimal totalMax,
                                              String promoCode) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();
            if (clientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("client").get("id"), clientId));
            }
            if (statut != null) {
                predicate = cb.and(predicate, cb.equal(root.get("statut"), statut));
            }
            if (dateFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateCommande"), dateFrom));
            }
            if (dateTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateCommande"), dateTo));
            }
            if (totalMin != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("totalTTC"), totalMin));
            }
            if (totalMax != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("totalTTC"), totalMax));
            }
            if (promoCode != null && !promoCode.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("codePromo"), promoCode));
            }
            return predicate;
        };
    }

    // montant restant recalculation moved to PaymentDomainService

    private boolean isFinal(StatutCommande statut) {
        return statut == StatutCommande.CANCELED || statut == StatutCommande.CONFIRMED || statut == StatutCommande.REJECTED;
    }

    private BigDecimal calculateLoyaltyDiscount(NiveauClient niveau, BigDecimal sousTotal) {
        if (niveau == null) return BigDecimal.ZERO;
        switch (niveau) {
            case SILVER:
                return sousTotal.compareTo(new BigDecimal("500")) >= 0
                        ? MoneyUtil.twoDecimals(sousTotal.multiply(new BigDecimal("0.05")))
                        : BigDecimal.ZERO;
            case GOLD:
                return sousTotal.compareTo(new BigDecimal("800")) >= 0
                        ? MoneyUtil.twoDecimals(sousTotal.multiply(new BigDecimal("0.10")))
                        : BigDecimal.ZERO;
            case PLATINUM:
                return sousTotal.compareTo(new BigDecimal("1200")) >= 0
                        ? MoneyUtil.twoDecimals(sousTotal.multiply(new BigDecimal("0.15")))
                        : BigDecimal.ZERO;
            case BASIC:
            default:
                return BigDecimal.ZERO;
        }
    }

    private NiveauClient recalculateTier(Client client) {
        int confirmed = client.getTotalCommandesConfirmees() == null ? 0 : client.getTotalCommandesConfirmees();
        BigDecimal spent = client.getMontantCumule() == null ? BigDecimal.ZERO : client.getMontantCumule();

        if (confirmed >= 20 || spent.compareTo(new BigDecimal("15000")) >= 0) return NiveauClient.PLATINUM;
        if (confirmed >= 10 || spent.compareTo(new BigDecimal("5000")) >= 0) return NiveauClient.GOLD;
        if (confirmed >= 3 || spent.compareTo(new BigDecimal("1000")) >= 0) return NiveauClient.SILVER;
        return NiveauClient.BASIC;
    }
}
