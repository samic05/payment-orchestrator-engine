package dev.samic.financial.payment.orchestrator.engine.application.service;

import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase.ProcessPaymentCommand;
import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentGateway;
import dev.samic.financial.payment.orchestrator.engine.application.port.output.PaymentRepository;
import dev.samic.financial.payment.orchestrator.engine.domain.exception.InvalidPaymentMethodException;
import dev.samic.financial.payment.orchestrator.engine.domain.model.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitario del caso de uso PaymentService.
 *
 * Usa @Mock para los output ports (repository y gateway).
 * Así testeamos SOLO la lógica de orquestación, aislada de
 * infraestructura. Esto es un test rápido (~ms), no necesita
 * levantar Spring ni MongoDB.
 *
 * @ExtendWith(MockitoExtension.class) inicializa los mocks
 * automáticamente — no necesitas MockitoAnnotations.openMocks().
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @Mock
    private PaymentGateway gateway;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("execute: approved payment saves twice (pending + approved)")
    void executeApprovedPayment() {
        // Arrange: el gateway aprueba
        var approvedStatus = new Approved("AUTH-999", LocalDateTime.now());
        when(gateway.authorize(any(Payment.class))).thenReturn(approvedStatus);
        when(repository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        var command = new ProcessPaymentCommand(
                "USD", new BigDecimal("50.00"), "CREDIT_CARD",
                Map.of("last4Digits", "1234", "holderName", "Jane")
        );

        // Act
        Payment result = paymentService.execute(command);

        // Assert
        assertInstanceOf(Approved.class, result.status());
        assertEquals("AUTH-999", ((Approved) result.status()).authorizationCode());

        // Verify: save se llamó 2 veces (1. Pending, 2. Approved)
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(repository, times(2)).save(captor.capture());

        var savedPayments = captor.getAllValues();
        assertInstanceOf(Pending.class, savedPayments.get(0).status());
        assertInstanceOf(Approved.class, savedPayments.get(1).status());
    }

    @Test
    @DisplayName("execute: rejected payment returns Rejected status")
    void executeRejectedPayment() {
        var rejectedStatus = new Rejected("Limit exceeded");
        when(gateway.authorize(any())).thenReturn(rejectedStatus);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new ProcessPaymentCommand(
                "EUR", new BigDecimal("2000.00"), "BANK_TRANSFER",
                Map.of("iban", "ES7921000813610123456789")
        );

        Payment result = paymentService.execute(command);

        assertInstanceOf(Rejected.class, result.status());
        assertEquals("Limit exceeded", ((Rejected) result.status()).reason());
    }

    @Test
    @DisplayName("execute: fraud alert payment returns FraudAlert status")
    void executeFraudAlertPayment() {
        when(gateway.authorize(any())).thenReturn(new FraudAlert(92));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new ProcessPaymentCommand(
                "USD", new BigDecimal("9999.99"), "DIGITAL_WALLET",
                Map.of("walletToken", "tok_abc", "provider", "ApplePay")
        );

        Payment result = paymentService.execute(command);

        assertInstanceOf(FraudAlert.class, result.status());
        assertEquals(92, ((FraudAlert) result.status()).riskScore());
    }

    @Test
    @DisplayName("execute: invalid payment method throws exception")
    void executeInvalidMethodThrows() {
        var command = new ProcessPaymentCommand(
                "USD", new BigDecimal("10.00"), "BITCOIN",
                Map.of()
        );

        assertThrows(InvalidPaymentMethodException.class,
                () -> paymentService.execute(command));

        // Verify: nunca llegó a persistir ni al gateway
        verifyNoInteractions(repository);
        verifyNoInteractions(gateway);
    }

    @Test
    @DisplayName("findById: delegates to repository")
    void findByIdDelegates() {
        var payment = new Payment("PAY-1",
                new Money(new BigDecimal("10"), java.util.Currency.getInstance("USD")),
                new CreditCard("0000", "Test"),
                new Pending(),
                LocalDateTime.now());

        when(repository.findById("PAY-1")).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentService.findById("PAY-1");

        assertTrue(result.isPresent());
        assertEquals("PAY-1", result.get().paymentId());
    }

    @Test
    @DisplayName("findById: returns empty when not found")
    void findByIdReturnsEmpty() {
        when(repository.findById("NOPE")).thenReturn(Optional.empty());

        assertTrue(paymentService.findById("NOPE").isEmpty());
    }
}
