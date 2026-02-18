package dev.samic.financial.payment.orchestrator.engine.application.port.input;

import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;

import java.util.Optional;

public interface GetPaymentUseCase {

    Optional<Payment> findById(String paymentId);
}
