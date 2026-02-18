package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO de entrada para crear un pago via REST.
 *
 * Es un record porque es puro dato inmutable. Spring Boot deserializa
 * el JSON directamente al constructor canónico del record.
 *
 * Ejemplo de JSON que lo mapea:
 * {
 *   "currency": "USD",
 *   "amount": 150.00,
 *   "paymentMethodType": "CREDIT_CARD",
 *   "methodDetails": {
 *     "last4Digits": "4242",
 *     "holderName": "John Doe"
 *   }
 * }
 */
public record PaymentRequest(
        String currency,
        BigDecimal amount,
        String paymentMethodType,
        Map<String, String> methodDetails
) {}
