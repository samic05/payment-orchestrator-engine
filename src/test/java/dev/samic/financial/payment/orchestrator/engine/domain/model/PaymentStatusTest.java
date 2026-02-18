package dev.samic.financial.payment.orchestrator.engine.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la sealed interface PaymentStatus y sus records.
 *
 * Demuestran:
 * - Compact constructor validation en records
 * - Inmutabilidad (equals/hashCode generados)
 * - Pattern matching con instanceof
 */
class PaymentStatusTest {

    @Nested
    @DisplayName("Pending")
    class PendingTest {

        @Test
        @DisplayName("two Pending instances are equal (no-arg record)")
        void twoInstancesAreEqual() {
            assertEquals(new Pending(), new Pending());
        }

        @Test
        @DisplayName("implements PaymentStatus")
        void implementsPaymentStatus() {
            assertInstanceOf(PaymentStatus.class, new Pending());
        }
    }

    @Nested
    @DisplayName("Approved")
    class ApprovedTest {

        @Test
        @DisplayName("stores authorization code and date")
        void storesFields() {
            var now = LocalDateTime.now();
            var approved = new Approved("AUTH-123", now);

            assertEquals("AUTH-123", approved.authorizationCode());
            assertEquals(now, approved.approvedAt());
        }

        @Test
        @DisplayName("rejects null authorization code")
        void rejectsNullCode() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Approved(null, LocalDateTime.now()));
        }

        @Test
        @DisplayName("rejects blank authorization code")
        void rejectsBlankCode() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Approved("  ", LocalDateTime.now()));
        }

        @Test
        @DisplayName("rejects null date")
        void rejectsNullDate() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Approved("AUTH-123", null));
        }
    }

    @Nested
    @DisplayName("Rejected")
    class RejectedTest {

        @Test
        @DisplayName("stores rejection reason")
        void storesReason() {
            var rejected = new Rejected("Insufficient funds");
            assertEquals("Insufficient funds", rejected.reason());
        }

        @Test
        @DisplayName("rejects null reason")
        void rejectsNullReason() {
            assertThrows(IllegalArgumentException.class, () -> new Rejected(null));
        }
    }

    @Nested
    @DisplayName("FraudAlert")
    class FraudAlertTest {

        @Test
        @DisplayName("stores risk score within valid range")
        void storesRiskScore() {
            assertEquals(75, new FraudAlert(75).riskScore());
        }

        @Test
        @DisplayName("accepts boundary values 0 and 100")
        void acceptsBoundaryValues() {
            assertDoesNotThrow(() -> new FraudAlert(0));
            assertDoesNotThrow(() -> new FraudAlert(100));
        }

        @Test
        @DisplayName("rejects negative risk score")
        void rejectsNegative() {
            assertThrows(IllegalArgumentException.class, () -> new FraudAlert(-1));
        }

        @Test
        @DisplayName("rejects risk score above 100")
        void rejectsAbove100() {
            assertThrows(IllegalArgumentException.class, () -> new FraudAlert(101));
        }
    }

    @Test
    @DisplayName("pattern matching switch covers all cases")
    void patternMatchingSwitch() {
        PaymentStatus status = new Approved("CODE", LocalDateTime.now());

        // Este switch es exhaustivo — si agregas un nuevo estado, no compila sin manejarlo
        String result = switch (status) {
            case Pending p -> "pending";
            case Approved a -> "approved:" + a.authorizationCode();
            case Rejected r -> "rejected:" + r.reason();
            case FraudAlert f -> "fraud:" + f.riskScore();
        };

        assertEquals("approved:CODE", result);
    }
}
