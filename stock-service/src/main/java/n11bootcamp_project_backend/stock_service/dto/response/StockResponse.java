package n11bootcamp_project_backend.stock_service.dto.response;

import java.util.UUID;

public record StockResponse(
        UUID id,
        UUID productId,
        Integer quantity
) {}