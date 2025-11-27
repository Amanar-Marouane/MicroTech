package com.restapi.microtech.service;

import com.restapi.microtech.contract.OrderItemServiceContract;
import com.restapi.microtech.contract.PaymentDomainServiceContract;
import com.restapi.microtech.contract.PromoServiceContract;
import com.restapi.microtech.dto.request.ArticleCommandeRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private CommandeRepository commandeRepository;
    @Mock private PaiementRepository paiementRepository;

    @Mock private CommandeMapper commandeMapper;
    @Mock private PaiementMapper paiementMapper;

    @Mock private PromoServiceContract promoService;
    @Mock private OrderItemServiceContract orderItemService;
    @Mock private PaymentDomainServiceContract paymentDomainService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        // Set TVA rate to 20%
        ReflectionTestUtils.setField(orderService, "tvaRate", new BigDecimal("0.20"));
    }

    private Client buildClient(NiveauClient tier) {
        Client c = new Client();
        c.setId(1L);
        c.setNiveauFidelite(tier);
        c.setMontantCumule(BigDecimal.ZERO);
        c.setTotalCommandesConfirmees(0);
        return c;
    }

    private Commande newCommandeFor(Client client) {
        Commande cmd = new Commande();
        cmd.setClient(client);
        cmd.setArticles(new ArrayList<>());
        cmd.setPaiements(new ArrayList<>());
        cmd.setDateCommande(new Date());
        cmd.setStatut(StatutCommande.PENDING);
        return cmd;
    }

    @Test
    void create_withSufficientStock_andPromoAndLoyalty_appliesDiscounts_andSetsPending() {
        // Arrange
        Long clientId = 10L;
        Client client = buildClient(NiveauClient.SILVER);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        CommandeRequest request = new CommandeRequest();
        List<ArticleCommandeRequest> items = new ArrayList<>();
        ArticleCommandeRequest i1 = new ArticleCommandeRequest();
        i1.setProduitId(100L); i1.setQuantite(2);
        items.add(i1);
        request.setArticles(items);
        request.setCodePromo("PROMO-AB12");

        Commande mapped = newCommandeFor(client);
        when(commandeMapper.toEntity(eq(request), eq(client))).thenReturn(mapped);

        // Order items build result: subtotal 1000, stock sufficient
        OrderItemServiceContract.BuildResult buildResult = new OrderItemServiceContract.BuildResult(List.of(), new BigDecimal("1000.00"), false);
        when(orderItemService.buildArticles(eq(mapped), eq(items))).thenReturn(buildResult);

        // Promo 5% on 1000 = 50
        when(promoService.computePromoDiscount(eq("PROMO-AB12"), eq(new BigDecimal("1000.00"))))
                .thenReturn(new BigDecimal("50.00"));

        // Save returns same entity
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));

        // Map to response by reflecting core fields
        when(commandeMapper.toResponse(any(Commande.class))).thenAnswer(inv -> {
            Commande c = inv.getArgument(0);
            CommandeResponse r = new CommandeResponse();
            r.setStatut(c.getStatut());
            r.setSousTotalHT(c.getSousTotalHT());
            r.setMontantRemiseFidelite(c.getMontantRemiseFidelite());
            r.setMontantRemisePromo(c.getMontantRemisePromo());
            r.setMontantRemiseTotal(c.getMontantRemiseTotal());
            r.setMontantHTApresRemise(c.getMontantHTApresRemise());
            r.setMontantTVA(c.getMontantTVA());
            r.setTotalTTC(c.getTotalTTC());
            r.setMontantRestant(c.getMontantRestant());
            r.setCodePromo(c.getCodePromo());
            return r;
        });

        // Act
        CommandeResponse resp = orderService.create(clientId, request);

        // Assert
        assertThat(resp.getStatut()).isEqualTo(StatutCommande.PENDING);
        assertThat(resp.getSousTotalHT()).isEqualByComparingTo("1000.00");
        // Loyalty SILVER 5% of 1000 = 50
        assertThat(resp.getMontantRemiseFidelite()).isEqualByComparingTo("50.00");
        assertThat(resp.getMontantRemisePromo()).isEqualByComparingTo("50.00");
        assertThat(resp.getMontantRemiseTotal()).isEqualByComparingTo("100.00");
        assertThat(resp.getMontantHTApresRemise()).isEqualByComparingTo("900.00");
        assertThat(resp.getMontantTVA()).isEqualByComparingTo("180.00");
        assertThat(resp.getTotalTTC()).isEqualByComparingTo("1080.00");
        assertThat(resp.getMontantRestant()).isEqualByComparingTo("1080.00");
        assertThat(resp.getCodePromo()).isEqualTo("PROMO-AB12");
    }

    @Test
    void create_withInsufficientStock_setsRejected() {
        Long clientId = 11L;
        Client client = buildClient(NiveauClient.BASIC);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        CommandeRequest request = new CommandeRequest();
        ArticleCommandeRequest i1 = new ArticleCommandeRequest();
        i1.setProduitId(101L); i1.setQuantite(5);
        request.setArticles(List.of(i1));

        Commande mapped = newCommandeFor(client);
        when(commandeMapper.toEntity(eq(request), eq(client))).thenReturn(mapped);

        OrderItemServiceContract.BuildResult buildResult = new OrderItemServiceContract.BuildResult(List.of(), new BigDecimal("200.00"), true);
        when(orderItemService.buildArticles(eq(mapped), anyList())).thenReturn(buildResult);
        // codePromo is null in this scenario, subtotal is 200.00
        when(promoService.computePromoDiscount(isNull(), eq(new BigDecimal("200.00"))))
                .thenReturn(BigDecimal.ZERO);
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeMapper.toResponse(any(Commande.class))).thenAnswer(inv -> {
            Commande c = inv.getArgument(0);
            CommandeResponse r = new CommandeResponse();
            r.setStatut(c.getStatut());
            return r;
        });

        CommandeResponse resp = orderService.create(clientId, request);
        assertThat(resp.getStatut()).isEqualTo(StatutCommande.REJECTED);
    }

    @Test
    void addPayment_whenOrderFinal_throws() {
        Commande cmd = new Commande();
        cmd.setId(1L);
        cmd.setStatut(StatutCommande.CONFIRMED);
        when(commandeRepository.findById(1L)).thenReturn(Optional.of(cmd));

        PaiementRequest req = new PaiementRequest();
        req.setMontant(new BigDecimal("100"));
        req.setTypePaiement(TypePaiement.ESPECES);

        assertThatThrownBy(() -> orderService.addPayment(1L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("commande finale");
        verifyNoInteractions(paymentDomainService);
    }

    @Test
    void addPayment_whenPending_delegatesToPaymentDomainService() {
        Commande cmd = new Commande();
        cmd.setId(2L);
        cmd.setStatut(StatutCommande.PENDING);
        when(commandeRepository.findById(2L)).thenReturn(Optional.of(cmd));

        PaiementRequest req = new PaiementRequest();
        req.setMontant(new BigDecimal("150"));
        req.setTypePaiement(TypePaiement.ESPECES);

        Paiement paiement = new Paiement();
        paiement.setId(99L);
        when(paymentDomainService.addPaymentForOrder(eq(cmd), eq(req))).thenReturn(paiement);
        when(paiementMapper.toResponse(eq(paiement))).thenReturn(new PaiementResponse());

        PaiementResponse resp = orderService.addPayment(2L, req);
        assertThat(resp).isNotNull();
        verify(paymentDomainService).addPaymentForOrder(eq(cmd), eq(req));
    }

    @Test
    void updatePaymentStatus_whenOrderFinal_throws() {
        Commande cmd = new Commande();
        cmd.setId(3L);
        cmd.setStatut(StatutCommande.CANCELED);

        Paiement p = new Paiement();
        p.setId(44L);
        p.setCommande(cmd);
        when(paiementRepository.findById(44L)).thenReturn(Optional.of(p));

        UpdatePaiementStatutRequest req = new UpdatePaiementStatutRequest();
        req.setStatut(StatutPaiement.ENCAISSE);

        assertThatThrownBy(() -> orderService.updatePaymentStatus(44L, req))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("commande finale");
        verifyNoInteractions(paymentDomainService);
    }

    @Test
    void confirm_whenNotPending_throws() {
        Commande cmd = new Commande();
        cmd.setId(5L);
        cmd.setStatut(StatutCommande.CANCELED);
        when(commandeRepository.findById(5L)).thenReturn(Optional.of(cmd));

        assertThatThrownBy(() -> orderService.confirm(5L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("en attente");
    }

    @Test
    void confirm_whenRemainingNotZero_throws() {
        Commande cmd = new Commande();
        cmd.setId(6L);
        cmd.setStatut(StatutCommande.PENDING);
        cmd.setMontantRestant(new BigDecimal("10.00"));
        when(commandeRepository.findById(6L)).thenReturn(Optional.of(cmd));

        assertThatThrownBy(() -> orderService.confirm(6L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("totalement payée");
    }

    @Test
    void confirm_happyPath_decrementsStock_updatesClient_andSetsConfirmed() {
        // Arrange
        Client client = buildClient(NiveauClient.BASIC);
        client.setMontantCumule(new BigDecimal("990.00")); // so after +10 becomes SILVER
        Commande cmd = new Commande();
        cmd.setId(7L);
        cmd.setClient(client);
        cmd.setStatut(StatutCommande.PENDING);
        cmd.setMontantRestant(BigDecimal.ZERO);
        cmd.setTotalTTC(new BigDecimal("10.00"));

        Produit prod = new Produit();
        prod.setId(77L);
        prod.setNom("Laptop");
        prod.setStockDisponible(5);

        ArticleCommande ac = new ArticleCommande();
        ac.setProduit(prod);
        ac.setQuantite(2);
        cmd.setArticles(List.of(ac));

        when(commandeRepository.findById(7L)).thenReturn(Optional.of(cmd));
        when(produitRepository.save(any(Produit.class))).thenAnswer(inv -> inv.getArgument(0));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(inv -> inv.getArgument(0));
        when(commandeMapper.toResponse(any(Commande.class))).thenAnswer(inv -> {
            Commande c = inv.getArgument(0);
            CommandeResponse r = new CommandeResponse();
            r.setStatut(c.getStatut());
            return r;
        });

        // Act
        CommandeResponse resp = orderService.confirm(7L);

        // Assert stock decremented
        ArgumentCaptor<Produit> prodCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository).save(prodCaptor.capture());
        assertThat(prodCaptor.getValue().getStockDisponible()).isEqualTo(3);

        // Client updated (orders count +1, montantCumule + 10, tier SILVER)
        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());
        Client updated = clientCaptor.getValue();
        assertThat(updated.getTotalCommandesConfirmees()).isEqualTo(1);
        assertThat(updated.getMontantCumule()).isEqualByComparingTo("1000.00");
        assertThat(updated.getNiveauFidelite()).isEqualTo(NiveauClient.SILVER);

        // Status set to CONFIRMED
        assertThat(resp.getStatut()).isEqualTo(StatutCommande.CONFIRMED);
    }
}
