package dev.samic.financial.payment.orchestrator.engine.infrastructure.adapter.output.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "payments")
public class PaymentDocument {

    @Id
    private String id;
    private BigDecimal amount;
    private String currency;
    private String methodType;
    private Map<String, String> methodDetails;
    private String statusType;
    private Map<String, String> statusDetails;
    private LocalDateTime createdAt;

    public PaymentDocument() {}

    public PaymentDocument(String id, BigDecimal amount, String currency,
                           String methodType, Map<String, String> methodDetails,
                           String statusType, Map<String, String> statusDetails,
                           LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.methodType = methodType;
        this.methodDetails = methodDetails;
        this.statusType = statusType;
        this.statusDetails = statusDetails;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getMethodType() { return methodType; }
    public Map<String, String> getMethodDetails() { return methodDetails; }
    public String getStatusType() { return statusType; }
    public Map<String, String> getStatusDetails() { return statusDetails; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
