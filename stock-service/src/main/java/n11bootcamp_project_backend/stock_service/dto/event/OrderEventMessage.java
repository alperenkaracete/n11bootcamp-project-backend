package n11bootcamp_project_backend.stock_service.dto.event;

import java.util.List;

public record OrderEventMessage(
        String orderId,
        List<OrderItemMessage> items
) {}