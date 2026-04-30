package n11bootcamp_project_backend.order_service.dto.request;

import jakarta.validation.constraints.NotNull;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;

public record OrderStatusUpdateRequest(
        @NotNull
        OrderStatus status
) {}