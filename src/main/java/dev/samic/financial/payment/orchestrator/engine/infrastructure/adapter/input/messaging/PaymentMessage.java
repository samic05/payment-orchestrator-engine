package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.messaging;

import java.math.BigDecimal;
import java.util.Map;

public record PaymentMessage(
        String currency,
        BigDecimal amount,
        String paymentMethodType,
        Map<String, String> methodDetails
) {}
