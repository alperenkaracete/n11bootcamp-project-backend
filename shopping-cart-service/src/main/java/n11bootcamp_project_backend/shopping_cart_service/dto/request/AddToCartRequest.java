package n11bootcamp_project_backend.shopping_cart_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record AddToCartRequest(

        @NotNull(message = "Product ID cannot be null")
        UUID productId,

        @NotBlank(message = "Product name cannot be blank")
        String productName,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}