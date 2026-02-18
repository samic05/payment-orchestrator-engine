package dev.samic.financial.payment.orchestrator.engine.domain.exception;

public class InvalidPaymentMethodException extends RuntimeException {

    public InvalidPaymentMethodException(String methodType) {
        super("Unsupported payment method type: " + methodType);
    }
}
