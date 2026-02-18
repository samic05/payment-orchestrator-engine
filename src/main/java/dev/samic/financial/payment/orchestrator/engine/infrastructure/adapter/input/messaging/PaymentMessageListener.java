package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.messaging;

import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase.ProcessPaymentCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageListener {

    private final ProcessPaymentUseCase processPayment;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void onMessage(PaymentMessage message) {
        log.info("Received payment message: currency={}, amount={}, method={}",
                message.currency(), message.amount(), message.paymentMethodType());

        try {
            var command = new ProcessPaymentCommand(
                    message.currency(),
                    message.amount(),
                    message.paymentMethodType(),
                    message.methodDetails()
            );

            var payment = processPayment.execute(command);
            log.info("Payment processed from queue: {}", payment.statusSummary());

        } catch (Exception ex) {
            // En producción aquí iría un dead-letter queue (DLQ) strategy.
            // Por ahora logueamos el error. El mensaje NO se re-encolará
            // porque no relanzamos la excepción (ack implícito).
            log.error("Failed to process payment message: {}", ex.getMessage(), ex);
        }
    }
}
