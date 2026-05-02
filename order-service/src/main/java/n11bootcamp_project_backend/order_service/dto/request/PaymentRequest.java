package n11bootcamp_project_backend.order_service.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

// Redis'e yazılacağı için Serializable arayüzünü (interface) implement etmesi çok önemlidir.
public record PaymentRequest(
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String cardHolderName,
        String cardNumber,
        String expireMonth,
        String expireYear,
        String cvc
) implements Serializable {}