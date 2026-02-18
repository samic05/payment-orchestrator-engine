package dev.samic.financial.payment.orchestrator.engine.domain.model;

public record CreditCard(String last4Digits, String holderName) implements PaymentMethod {

    public CreditCard {
        if (last4Digits == null || !last4Digits.matches("\\d{4}")) {
            throw new IllegalArgumentException("last4Digits must be exactly 4 digits");
        }
        if (holderName == null || holderName.isBlank()) {
            throw new IllegalArgumentException("Holder name cannot be null or blank");
        }
    }
}
