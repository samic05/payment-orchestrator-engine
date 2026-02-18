package dev.samic.financial.payment.orchestrator.engine.domain.model;

public sealed interface PaymentStatus
        permits Pending, Approved, Rejected, FraudAlert {
}
