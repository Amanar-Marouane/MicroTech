package com.restapi.microtech.dto.response;

import com.restapi.microtech.entity.enums.NiveauClient;
import lombok.Data;

@Data
public class LoyaltyTierResponse {
    private Long clientId;
    private NiveauClient niveauFidelite;
}
