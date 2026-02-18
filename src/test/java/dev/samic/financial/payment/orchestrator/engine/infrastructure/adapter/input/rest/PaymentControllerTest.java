package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.input.rest;

import dev.samic.financial.payment.orchestrator.engine.application.port.input.GetPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.application.port.input.ProcessPaymentUseCase;
import dev.samic.financial.payment.orchestrator.engine.domain.model.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de la capa web con @WebMvcTest.
 *
 * @WebMvcTest solo levanta el contexto MVC (controller + exception handler).
 * NO levanta MongoDB, RabbitMQ, ni services reales.
 * Los use cases se mockean con @MockitoBean.
 *
 * Esto testea:
 * - Deserialización del JSON request
 * - Serialización del JSON response
 * - HTTP status codes (201 Created, 200 OK, 404 Not Found)
 * - El GlobalExceptionHandler
 */
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessPaymentUseCase processPayment;

    @MockitoBean
    private GetPaymentUseCase getPayment;

    private final Payment sampleApproved = new Payment(
            "PAY-100",
            new Money(new BigDecimal("250.00"), Currency.getInstance("USD")),
            new CreditCard("4242", "John Doe"),
            new Approved("AUTH-ABC", LocalDateTime.of(2025, 6, 15, 12, 0)),
            LocalDateTime.of(2025, 6, 15, 11, 55)
    );

    @Test
    @DisplayName("POST /api/v1/payments → 201 Created with Approved response")
    void createPaymentReturns201() throws Exception {
        when(processPayment.execute(any())).thenReturn(sampleApproved);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "currency": "USD",
                                    "amount": 250.00,
                                    "paymentMethodType": "CREDIT_CARD",
                                    "methodDetails": {
                                        "last4Digits": "4242",
                                        "holderName": "John Doe"
                                    }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value("PAY-100"))
                .andExpect(jsonPath("$.status").value("Approved"))
                .andExpect(jsonPath("$.statusDetails.authorizationCode").value("AUTH-ABC"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    @DisplayName("GET /api/v1/payments/{id} → 200 OK when found")
    void getPaymentReturns200() throws Exception {
        when(getPayment.findById("PAY-100")).thenReturn(Optional.of(sampleApproved));

        mockMvc.perform(get("/api/v1/payments/PAY-100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value("PAY-100"))
                .andExpect(jsonPath("$.status").value("Approved"));
    }

    @Test
    @DisplayName("GET /api/v1/payments/{id} → 404 Not Found when missing")
    void getPaymentReturns404() throws Exception {
        when(getPayment.findById("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/payments/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Payment Not Found"))
                .andExpect(jsonPath("$.detail").value("Payment not found: UNKNOWN"));
    }
}
