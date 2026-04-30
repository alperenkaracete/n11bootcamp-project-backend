package n11bootcamp_project_backend.order_service.dto.request;

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
        BigDecimal totalPrice
) {}
