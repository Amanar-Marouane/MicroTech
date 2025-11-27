package com.restapi.microtech.service;

import com.restapi.microtech.contract.ClientServiceContract;
import com.restapi.microtech.dto.request.ClientRequest;
import com.restapi.microtech.dto.response.ClientResponse;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.ClientStatsResponse;
import com.restapi.microtech.dto.response.LoyaltyTierResponse;
import com.restapi.microtech.entity.Client;
import com.restapi.microtech.entity.enums.UserRole;
import com.restapi.microtech.mapper.ClientMapper;
import com.restapi.microtech.mapper.CommandeMapper;
import com.restapi.microtech.repository.ClientRepository;
import com.restapi.microtech.repository.CommandeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService implements ClientServiceContract {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CommandeMapper commandeMapper;
    private final CommandeRepository commandeRepository;

    @Override
    public ClientResponse createClient(ClientRequest request) {
        Client client = clientMapper.toEntity(request);
        client.setRole(UserRole.CLIENT);
        clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientMapper.toResponse(client);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .filter(u -> u instanceof Client)
                .map(u -> clientMapper.toResponse((Client) u))
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponse updateClient(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        clientMapper.updateEntity(request, client);
        clientRepository.save(client);
        return clientMapper.toResponse(client);
    }

    @Override
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @Override
    public ClientResponse getProfile(Long id) {
        return getClientById(id);
    }

    @Override
    public LoyaltyTierResponse getLoyaltyTier(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        LoyaltyTierResponse r = new LoyaltyTierResponse();
        r.setClientId(client.getId());
        r.setNiveauFidelite(client.getNiveauFidelite());
        return r;
    }

    @Override
    public ClientStatsResponse getStatistics(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        ClientStatsResponse stats = new ClientStatsResponse();
        stats.setClientId(client.getId());
        stats.setTotalCommandesConfirmees(client.getTotalCommandesConfirmees());
        stats.setMontantCumule(client.getMontantCumule());
        stats.setDatePremiereCommande(client.getDatePremiereCommande());
        stats.setDateDerniereCommande(client.getDateDerniereCommande());
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeResponse> getOrderHistory(Long id) {
        // Fetch with details to avoid LazyInitializationException
        List<com.restapi.microtech.entity.Commande> commandes = commandeRepository.findWithDetailsByClientId(id);
        return commandes.stream().map(commandeMapper::toResponse).toList();
    }
}
