package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.persistence;

import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentRepository;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> store = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        store.put(payment.paymentId(), payment);
        log.debug("Saved payment: {}", payment.paymentId());
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(store.get(paymentId));
    }
}
