package com.restapi.microtech.dto.simple;

import com.restapi.microtech.entity.enums.StatutCommande;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CommandeSimple {

    private Long id;
    private Date dateCommande;
    private BigDecimal totalTTC;
    private StatutCommande statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}