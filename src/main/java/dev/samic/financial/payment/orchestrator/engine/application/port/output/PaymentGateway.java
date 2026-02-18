package dev.samic.financial.payment.orchestrator.engine.application.port.output;

import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;
import dev.samic.financial.payment.orchestrator.engine.domain.model.PaymentStatus;

public interface PaymentGateway {

    PaymentStatus authorize(Payment payment);
}
