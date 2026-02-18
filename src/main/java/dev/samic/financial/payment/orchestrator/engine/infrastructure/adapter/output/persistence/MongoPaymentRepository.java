package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.persistence;

import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentRepository;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class MongoPaymentRepository implements PaymentRepository {

    private final SpringDataMongoPaymentRepository mongoRepository;

    @Override
    public Payment save(Payment payment) {
        PaymentDocument document = PaymentDocumentMapper.toDocument(payment);
        mongoRepository.save(document);
        log.debug("Persisted payment to MongoDB: {}", payment.paymentId());
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return mongoRepository.findById(paymentId)
                .map(PaymentDocumentMapper::toDomain);
    }
}
