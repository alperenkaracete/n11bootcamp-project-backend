package n11bootcamp_project_backend.stock_service.dto.event;

public record OrderItemMessage(
        String productId,
        Integer quantity
) {}