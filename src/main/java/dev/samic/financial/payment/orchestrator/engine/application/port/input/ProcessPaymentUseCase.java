package dev.samic.financial.payment.orchestrator.engine.application.port.input;

import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;

public interface ProcessPaymentUseCase {

    Payment execute(ProcessPaymentCommand command);

    record ProcessPaymentCommand(
            String currency,
            java.math.BigDecimal amount,
            String paymentMethodType,
            java.util.Map<String, String> methodDetails
    ) {}
}
