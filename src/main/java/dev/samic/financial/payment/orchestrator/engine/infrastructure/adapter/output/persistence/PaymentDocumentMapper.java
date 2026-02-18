package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.persistence;

import dev.samic.financial.payment.orchestrator.engine.domain.model.*;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Map;

public final class PaymentDocumentMapper {

    private PaymentDocumentMapper() {}

    public static PaymentDocument toDocument(Payment payment) {
        return new PaymentDocument(
                payment.paymentId(),
                payment.amount().amount(),
                payment.amount().currency().getCurrencyCode(),
                mapMethodType(payment.method()),
                mapMethodDetails(payment.method()),
                mapStatusType(payment.status()),
                mapStatusDetails(payment.status()),
                payment.createdAt()
        );
    }

    public static Payment toDomain(PaymentDocument doc) {
        return new Payment(
                doc.getId(),
                new Money(doc.getAmount(), Currency.getInstance(doc.getCurrency())),
                rebuildMethod(doc.getMethodType(), doc.getMethodDetails()),
                rebuildStatus(doc.getStatusType(), doc.getStatusDetails()),
                doc.getCreatedAt()
        );
    }


    private static String mapMethodType(PaymentMethod method) {
        return switch (method) {
            case CreditCard c -> "CREDIT_CARD";
            case BankTransfer b -> "BANK_TRANSFER";
            case DigitalWallet d -> "DIGITAL_WALLET";
        };
    }

    private static Map<String, String> mapMethodDetails(PaymentMethod method) {
        return switch (method) {
            case CreditCard c -> Map.of("last4Digits", c.last4Digits(), "holderName", c.holderName());
            case BankTransfer b -> Map.of("iban", b.iban());
            case DigitalWallet d -> Map.of("walletToken", d.walletToken(), "provider", d.provider());
        };
    }


    private static PaymentMethod rebuildMethod(String type, Map<String, String> details) {
        return switch (type) {
            case "CREDIT_CARD" -> new CreditCard(details.get("last4Digits"), details.get("holderName"));
            case "BANK_TRANSFER" -> new BankTransfer(details.get("iban"));
            case "DIGITAL_WALLET" -> new DigitalWallet(details.get("walletToken"), details.get("provider"));
            default -> throw new IllegalStateException("Unknown method type in DB: " + type);
        };
    }


    private static String mapStatusType(PaymentStatus status) {
        return switch (status) {
            case Pending p -> "PENDING";
            case Approved a -> "APPROVED";
            case Rejected r -> "REJECTED";
            case FraudAlert f -> "FRAUD_ALERT";
        };
    }

    private static Map<String, String> mapStatusDetails(PaymentStatus status) {
        return switch (status) {
            case Pending p -> Map.of();
            case Approved a -> Map.of(
                    "authorizationCode", a.authorizationCode(),
                    "approvedAt", a.approvedAt().toString()
            );
            case Rejected r -> Map.of("reason", r.reason());
            case FraudAlert f -> Map.of("riskScore", String.valueOf(f.riskScore()));
        };
    }


    private static PaymentStatus rebuildStatus(String type, Map<String, String> details) {
        return switch (type) {
            case "PENDING" -> new Pending();
            case "APPROVED" -> new Approved(details.get("authorizationCode"),
                    LocalDateTime.parse(details.get("approvedAt")));
            case "REJECTED" -> new Rejected(details.get("reason"));
            case "FRAUD_ALERT" -> new FraudAlert(Integer.parseInt(details.get("riskScore")));
            default -> throw new IllegalStateException("Unknown status type in DB: " + type);
        };
    }
}
