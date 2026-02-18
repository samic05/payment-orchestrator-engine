package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

interface SpringDataMongoPaymentRepository extends MongoRepository<PaymentDocument, String> {
}
