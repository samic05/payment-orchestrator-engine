package dev.samic.financial.payment.orchestrator.engine.domain.model;

import java.time.LocalDateTime;

public record Payment(
        String paymentId,
        Money amount,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime createdAt
) {

    public Payment {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Payment status cannot be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Created date cannot be null");
        }
    }


    public Payment withStatus(PaymentStatus newStatus) {
        return new Payment(paymentId, amount, method, newStatus, createdAt);
    }

    public String statusSummary() {
        return switch (status) {
            case Pending p -> "Payment %s is pending".formatted(paymentId);
            case Approved a -> "Payment %s approved with code %s".formatted(paymentId, a.authorizationCode());
            case Rejected r -> "Payment %s rejected: %s".formatted(paymentId, r.reason());
            case FraudAlert f -> "Payment %s flagged for fraud (risk: %d)".formatted(paymentId, f.riskScore());
        };
    }
}
