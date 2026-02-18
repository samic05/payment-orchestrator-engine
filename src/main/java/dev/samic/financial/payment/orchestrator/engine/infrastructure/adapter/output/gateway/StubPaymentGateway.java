package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.gateway;

import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentGateway;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Approved;
import dev.samic.financial.payment.orchestrator.engine.domain.model.FraudAlert;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;
import dev.samic.financial.payment.orchestrator.engine.domain.model.PaymentStatus;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Rejected;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class StubPaymentGateway implements PaymentGateway {

    @Override
    public PaymentStatus authorize(Payment payment) {
        BigDecimal amount = payment.amount().amount();
        log.info("Authorizing payment {} for {} {}",
                payment.paymentId(), amount, payment.amount().currency());

        simulateLatency();

        if (amount.compareTo(new BigDecimal("5000")) >= 0) {
            return new FraudAlert(85);
        }
        if (amount.compareTo(new BigDecimal("1000")) >= 0) {
            return new Rejected("Amount exceeds single transaction limit");
        }
        return new Approved(
                "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                LocalDateTime.now()
        );
    }

    private void simulateLatency() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
