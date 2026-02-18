package dev.samic.financial.payment.orchestrator.engine.application.service;

import dev.samic.financial.payment.orchestrator.engine.application.port.input.GetPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentGateway;
import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentRepository;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Money;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;
import dev.samic.financial.payment.orchestrator.engine.domain.model.PaymentMethod;
import dev.samic.financial.payment.orchestrator.engine.domain.model.PaymentStatus;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Pending;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements ProcessPaymentUseCase, GetPaymentUseCase {

    private final PaymentRepository repository;
    private final PaymentGateway gateway;

    @Override
    public Payment execute(ProcessPaymentCommand command) {
        log.info("Processing payment: currency={}, amount={}, method={}",
                command.currency(), command.amount(), command.paymentMethodType());

        PaymentMethod method = PaymentMethodFactory.create(
                command.paymentMethodType(),
                command.methodDetails()
        );

        Money money = new Money(command.amount(), Currency.getInstance(command.currency()));

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                money,
                method,
                new Pending(),
                LocalDateTime.now()
        );

        repository.save(payment);
        log.info("Payment created: id={}", payment.paymentId());

        PaymentStatus resolvedStatus = gateway.authorize(payment);
        log.info("Gateway response for {}: {}", payment.paymentId(), resolvedStatus);

        Payment processedPayment = payment.withStatus(resolvedStatus);

        repository.save(processedPayment);
        log.info("Payment processed: {}", processedPayment.statusSummary());

        return processedPayment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return repository.findById(paymentId);
    }
}
