package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest;

import dev.samic.financial.payment.orchestrator.engine.domain.exception.InvalidPaymentMethodException;
import dev.samic.financial.payment.orchestrator.engine.domain.exception.PaymentNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handleNotFound(PaymentNotFoundException ex) {
        log.warn("Payment not found: {}", ex.getPaymentId());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Payment Not Found");
        problem.setType(URI.create("https://api.payments.dev/errors/not-found"));
        return problem;
    }

    @ExceptionHandler(InvalidPaymentMethodException.class)
    public ProblemDetail handleInvalidMethod(InvalidPaymentMethodException ex) {
        log.warn("Invalid payment method: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Payment Method");
        problem.setType(URI.create("https://api.payments.dev/errors/invalid-method"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleValidation(IllegalArgumentException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://api.payments.dev/errors/validation"));
        return problem;
    }
}
