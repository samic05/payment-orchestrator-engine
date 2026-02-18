package dev.samic.financial.payment.orchestrator.engine.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la entidad Payment.
 * Demuestran el patrón de inmutabilidad con withStatus().
 */
class PaymentTest {

    private final Payment samplePayment = new Payment(
            "PAY-001",
            new Money(new BigDecimal("100.00"), Currency.getInstance("USD")),
            new CreditCard("4242", "John Doe"),
            new Pending(),
            LocalDateTime.of(2025, 1, 15, 10, 30)
    );

    @Test
    @DisplayName("withStatus creates a new instance without modifying the original")
    void withStatusIsImmutable() {
        var approved = new Approved("AUTH-X", LocalDateTime.now());

        Payment updated = samplePayment.withStatus(approved);

        // El original sigue siendo Pending
        assertInstanceOf(Pending.class, samplePayment.status());
        // El nuevo es Approved
        assertInstanceOf(Approved.class, updated.status());
        // Mismo ID, diferente estado
        assertEquals(samplePayment.paymentId(), updated.paymentId());
    }

    @Test
    @DisplayName("statusSummary uses pattern matching for each status")
    void statusSummaryPending() {
        assertTrue(samplePayment.statusSummary().contains("pending"));
    }

    @Test
    @DisplayName("statusSummary for Approved includes auth code")
    void statusSummaryApproved() {
        var approved = samplePayment.withStatus(new Approved("AUTH-ABC", LocalDateTime.now()));
        assertTrue(approved.statusSummary().contains("AUTH-ABC"));
    }

    @Test
    @DisplayName("statusSummary for Rejected includes reason")
    void statusSummaryRejected() {
        var rejected = samplePayment.withStatus(new Rejected("Limit exceeded"));
        assertTrue(rejected.statusSummary().contains("Limit exceeded"));
    }

    @Test
    @DisplayName("statusSummary for FraudAlert includes risk score")
    void statusSummaryFraudAlert() {
        var fraud = samplePayment.withStatus(new FraudAlert(90));
        assertTrue(fraud.statusSummary().contains("90"));
    }

    @Test
    @DisplayName("rejects null payment ID")
    void rejectsNullId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Payment(null, samplePayment.amount(), samplePayment.method(),
                        samplePayment.status(), samplePayment.createdAt()));
    }

    @Test
    @DisplayName("Money rejects negative amount")
    void moneyRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Money(new BigDecimal("-1"), Currency.getInstance("USD")));
    }
}
