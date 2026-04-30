package n11bootcamp_project_backend.order_service.dto.response;

import n11bootcamp_project_backend.order_service.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        OrderStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {}