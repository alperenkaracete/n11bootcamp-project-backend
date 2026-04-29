package n11bootcamp_project_backend.shopping_cart_service.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID userId,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {}