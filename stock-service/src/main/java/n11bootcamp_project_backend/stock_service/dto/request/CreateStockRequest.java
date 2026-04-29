package n11bootcamp_project_backend.stock_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStockRequest(

        @NotNull(message = "Product ID cannot be null")
        UUID productId,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity
) {}