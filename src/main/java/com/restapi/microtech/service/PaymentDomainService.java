package com.restapi.microtech.service;

import com.restapi.microtech.contract.PaymentDomainServiceContract;
import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.request.UpdatePaiementStatutRequest;
import com.restapi.microtech.entity.Commande;
import com.restapi.microtech.entity.Paiement;
import com.restapi.microtech.entity.enums.StatutPaiement;
import com.restapi.microtech.entity.enums.TypePaiement;
import com.restapi.microtech.exception.BusinessRuleException;
import com.restapi.microtech.mapper.PaiementMapper;
import com.restapi.microtech.repository.CommandeRepository;
import com.restapi.microtech.repository.PaiementRepository;
import com.restapi.microtech.util.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentDomainService implements PaymentDomainServiceContract {

    private final PaiementRepository paiementRepository;
    private final CommandeRepository commandeRepository;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional
    public Paiement addPaymentForOrder(Commande commande, PaiementRequest request) {
        if (request.getTypePaiement() == TypePaiement.ESPECES && request.getMontant() != null
                && request.getMontant().compareTo(new BigDecimal("20000")) > 0) {
            throw new BusinessRuleException("Paiement en espèces limité à 20,000 DH");
        }

        Paiement paiement = paiementMapper.toEntity(request, commande);
        int next = commande.getPaiements() == null ? 1 : commande.getPaiements().size() + 1;
        paiement.setNumeroPaiement(next);

        Paiement saved = paiementRepository.save(paiement);
        recalcMontantRestant(commande);
        return saved;
    }

    @Override
    @Transactional
    public Paiement updatePaymentStatus(Paiement paiement, UpdatePaiementStatutRequest request) {
        paiement.setStatut(request.getStatut());
        paiement.setDateEncaissement(request.getDateEncaissement());
        Paiement saved = paiementRepository.save(paiement);
        recalcMontantRestant(saved.getCommande());
        return saved;
    }

    private void recalcMontantRestant(Commande cmd) {
        List<Paiement> paiements = cmd.getPaiements();
        BigDecimal encaisse = BigDecimal.ZERO;
        if (paiements != null) {
            for (Paiement p : paiements) {
                if (p.getStatut() == StatutPaiement.ENCAISSE) {
                    encaisse = encaisse.add(p.getMontant());
                }
            }
        }
        BigDecimal restant = cmd.getTotalTTC().subtract(encaisse);
        if (restant.compareTo(BigDecimal.ZERO) < 0) restant = BigDecimal.ZERO;
        cmd.setMontantRestant(MoneyUtil.twoDecimals(restant));
        commandeRepository.save(cmd);
    }
}
