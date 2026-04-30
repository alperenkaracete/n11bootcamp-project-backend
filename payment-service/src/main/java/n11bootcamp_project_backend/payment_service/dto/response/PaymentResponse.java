package n11bootcamp_project_backend.payment_service.dto.response;

import n11bootcamp_project_backend.payment_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        PaymentStatus status,
        LocalDateTime createdAt
) {}