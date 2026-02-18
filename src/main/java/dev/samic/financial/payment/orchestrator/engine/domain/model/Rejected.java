package dev.samic.financial.payment.orchestrator.engine.domain.model;

public record Rejected(String reason) implements PaymentStatus {

    public Rejected {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Rejection reason cannot be null or blank");
        }
    }
}
