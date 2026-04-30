package n11bootcamp_project_backend.payment_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(

        @NotNull(message = "Order ID cannot be null")
        UUID orderId,

        @NotNull(message = "User ID cannot be null")
        UUID userId,

        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        // Kart bilgileri
        @NotBlank(message = "Card holder name cannot be blank")
        String cardHolderName,

        @NotBlank(message = "Card number cannot be blank")
        String cardNumber,

        @NotBlank(message = "Expire month cannot be blank")
        String expireMonth,

        @NotBlank(message = "Expire year cannot be blank")
        String expireYear,

        @NotBlank(message = "CVC cannot be blank")
        String cvc
) {}