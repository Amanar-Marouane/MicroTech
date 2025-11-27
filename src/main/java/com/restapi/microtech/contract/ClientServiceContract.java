package com.restapi.microtech.contract;

import com.restapi.microtech.dto.request.ClientRequest;
import com.restapi.microtech.dto.response.ClientResponse;
import com.restapi.microtech.dto.response.ClientStatsResponse;
import com.restapi.microtech.dto.response.CommandeResponse;
import com.restapi.microtech.dto.response.LoyaltyTierResponse;
import java.util.List;

public interface ClientServiceContract {
    ClientResponse createClient(ClientRequest request);

    ClientResponse getClientById(Long id);

    List<ClientResponse> getAllClients();

    ClientResponse updateClient(Long id, ClientRequest request);

    void deleteClient(Long id);

    // Dedicated client endpoints
    ClientResponse getProfile(Long id);

    LoyaltyTierResponse getLoyaltyTier(Long id);

    ClientStatsResponse getStatistics(Long id);

    List<CommandeResponse> getOrderHistory(Long id);
}
