package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest.dto;

import dev.samic.financial.payment.orchestrator.engine.domain.model.Approved;
import dev.samic.financial.payment.orchestrator.engine.domain.model.FraudAlert;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Pending;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Rejected;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO de respuesta que se serializa a JSON.
 *
 * El factory method fromDomain() traduce la entidad Payment al DTO.
 * Usa Pattern Matching con switch para extraer datos específicos
 * de cada estado del sealed PaymentStatus.
 *
 * Esto demuestra el poder del combo sealed + pattern matching:
 * el compilador verifica que manejas TODOS los estados posibles.
 */
public record PaymentResponse(
        String paymentId,
        BigDecimal amount,
        String currency,
        String status,
        Map<String, Object> statusDetails,
        LocalDateTime createdAt
) {

    public static PaymentResponse fromDomain(Payment payment) {
        // Pattern matching con switch exhaustivo sobre sealed interface
        Map<String, Object> details = switch (payment.status()) {
            case Pending p -> Map.of();
            case Approved a -> Map.of(
                    "authorizationCode", a.authorizationCode(),
                    "approvedAt", a.approvedAt().toString()
            );
            case Rejected r -> Map.of(
                    "reason", r.reason()
            );
            case FraudAlert f -> Map.of(
                    "riskScore", f.riskScore()
            );
        };

        // Obtener el nombre simple del estado (ej: "Approved", "Rejected")
        String statusName = payment.status().getClass().getSimpleName();

        return new PaymentResponse(
                payment.paymentId(),
                payment.amount().amount(),
                payment.amount().currency().getCurrencyCode(),
                statusName,
                details,
                payment.createdAt()
        );
    }
}
