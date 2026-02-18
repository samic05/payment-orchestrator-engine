package dev.samic.financial.payment.orchestrator.engine.domain.model;

public record DigitalWallet(String walletToken, String provider) implements PaymentMethod {

    public DigitalWallet {
        if (walletToken == null || walletToken.isBlank()) {
            throw new IllegalArgumentException("Wallet token cannot be null or blank");
        }
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Provider cannot be null or blank");
        }
    }
}
