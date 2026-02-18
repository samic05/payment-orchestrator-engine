package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest;

import dev.samic.financial.payment.orchestrator.engine.application.port.input.GetPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase.ProcessPaymentCommand;
import dev.samic.financial.payment.orchestrator.engine.domain.exception.PaymentNotFoundException;
import dev.samic.financial.payment.orchestrator.engine.domain.model.Payment;
import dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest.dto.PaymentRequest;
import dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest.dto.PaymentResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentUseCase processPayment;
    private final GetPaymentUseCase getPayment;

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        var command = new ProcessPaymentCommand(
                request.currency(),
                request.amount(),
                request.paymentMethodType(),
                request.methodDetails()
        );

        Payment payment = processPayment.execute(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponse.fromDomain(payment));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable String paymentId) {
        Payment payment = getPayment.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        return ResponseEntity.ok(PaymentResponse.fromDomain(payment));
    }
}
