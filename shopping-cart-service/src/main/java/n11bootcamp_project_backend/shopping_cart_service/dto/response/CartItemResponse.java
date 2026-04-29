package n11bootcamp_project_backend.shopping_cart_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity,
        BigDecimal subTotal
) {}