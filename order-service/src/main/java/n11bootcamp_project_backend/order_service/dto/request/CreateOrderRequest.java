package n11bootcamp_project_backend.order_service.dto.request; // Kendi paket adına göre ayarla

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        @NotNull(message = "User ID cannot be null")
        UUID userId,

        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemRequest> items,

        @NotNull(message = "Total price cannot be null")
        BigDecimal totalPrice,

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