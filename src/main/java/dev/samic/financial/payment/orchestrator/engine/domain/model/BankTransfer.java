package dev.samic.financial.payment.orchestrator.engine.domain.model;

public record BankTransfer(String iban) implements PaymentMethod {

    public BankTransfer {
        if (iban == null || iban.isBlank()) {
            throw new IllegalArgumentException("IBAN cannot be null or blank");
        }
    }
}
