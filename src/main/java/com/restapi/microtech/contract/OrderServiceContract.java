package com.restapi.microtech.contract;

import com.restapi.microtech.dto.request.CommandeRequest;
import com.restapi.microtech.dto.request.PaiementRequest;
import com.restapi.microtech.dto.request.UpdatePaiementStatutRequest;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.PaiementResponse;
import com.restapi.microtech.entity.enums.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderServiceContract {
    CommandeResponse create(Long clientId, CommandeRequest request);

    CommandeResponse getById(Long id);

    Page<CommandeResponse> search(Long clientId,
                                  StatutCommande statut,
                                  java.util.Date dateFrom,
                                  java.util.Date dateTo,
                                  java.math.BigDecimal totalMin,
                                  java.math.BigDecimal totalMax,
                                  String promoCode,
                                  Pageable pageable);

    PaiementResponse addPayment(Long commandeId, PaiementRequest request);

    PaiementResponse updatePaymentStatus(Long paiementId, UpdatePaiementStatutRequest request);

    CommandeResponse confirm(Long id);

    CommandeResponse cancel(Long id);
}
