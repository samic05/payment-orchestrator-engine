package dev.samic.financial.payment.orchestrator.engine.application.port.output;

import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String paymentId);
}
