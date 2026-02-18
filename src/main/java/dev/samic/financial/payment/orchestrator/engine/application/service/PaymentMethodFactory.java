package dev.samic.financial.payment.orchestrator.engine.application.service;

import dev.samic.financial.payment.orchestrator.engine.domain.exception.InvalidPaymentMethodException;
import dev.samic.financial.payment.orchestrator.engine.domain.model.BankTransfer;
import dev.samic.financial.payment.orchestrator.engine.domain.model.CreditCard;
import dev.samic.financial.payment.orchestrator.engine.domain.model.DigitalWallet;
import dev.samic.financial.payment.orchestrator.engine.domain.model.PaymentMethod;

import java.util.Map;

public final class PaymentMethodFactory {

    private PaymentMethodFactory() {}

    public static PaymentMethod create(String type, Map<String, String> details) {
        return switch (type.toUpperCase()) {
            case "CREDIT_CARD" -> new CreditCard(
                    details.get("last4Digits"),
                    details.get("holderName")
            );
            case "BANK_TRANSFER" -> new BankTransfer(
                    details.get("iban")
            );
            case "DIGITAL_WALLET" -> new DigitalWallet(
                    details.get("walletToken"),
                    details.get("provider")
            );
            default -> throw new InvalidPaymentMethodException(type);
        };
    }
}
