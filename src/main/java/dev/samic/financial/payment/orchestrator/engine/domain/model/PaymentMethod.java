package dev.samic.financial.payment.orchestrator.engine.domain.model;

public sealed interface PaymentMethod
        permits CreditCard, BankTransfer, DigitalWallet {
}
