package com.restapi.microtech.contract;

import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.request.UpdatePaiementStatutRequest;
import com.restapi.microtech.entity.Commande;
import com.restapi.microtech.entity.Paiement;

public interface PaymentDomainServiceContract {
    /** Creates and validates a new Paiement for the order, assigns sequence number, persists, and recalculates remaining amount. */
    Paiement addPaymentForOrder(Commande commande, PaiementRequest request);

    /** Updates statut/dates for a Paiement, persists, and recalculates remaining amount on its order. */
    Paiement updatePaymentStatus(Paiement paiement, UpdatePaiementStatutRequest request);
}
