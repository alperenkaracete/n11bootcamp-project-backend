package n11bootcamp_project_backend.order_service.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity
) {}