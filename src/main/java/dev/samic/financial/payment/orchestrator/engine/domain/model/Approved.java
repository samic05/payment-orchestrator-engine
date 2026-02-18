package dev.samic.financial.payment.orchestrator.engine.domain.model;

import java.time.LocalDateTime;


public record Approved(String authorizationCode, LocalDateTime approvedAt) implements PaymentStatus {

    public Approved {
        if (authorizationCode == null || authorizationCode.isBlank()) {
            throw new IllegalArgumentException("Authorization code cannot be null or blank");
        }
        if (approvedAt == null) {
            throw new IllegalArgumentException("Approved date cannot be null");
        }
    }
}
