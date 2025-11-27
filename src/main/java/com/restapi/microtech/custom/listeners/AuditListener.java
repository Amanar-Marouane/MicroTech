package com.restapi.microtech.custom.listeners;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

import com.restapi.microtech.custom.CreatedAt;
import com.restapi.microtech.custom.UpdatedAt;
import com.restapi.microtech.custom.util.Helpers;

public class AuditListener {

    @PrePersist
    public void setCreatedAt(Object entity) {
        Helpers.setFieldIfPresent(entity, CreatedAt.class, LocalDateTime.now());
        Helpers.setFieldIfPresent(entity, UpdatedAt.class, LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdatedAt(Object entity) {
        Helpers.setFieldIfPresent(entity, UpdatedAt.class, LocalDateTime.now());
    }
    
}
