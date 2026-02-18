package dev.samic.financial.payment.orchestrator.engine.domain.model;

public record FraudAlert(int riskScore) implements PaymentStatus {

    public FraudAlert {
        if (riskScore < 0 || riskScore > 100) {
            throw new IllegalArgumentException("Risk score must be between 0 and 100");
        }
    }
}
