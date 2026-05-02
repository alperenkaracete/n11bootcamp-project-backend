package n11bootcamp_project_backend.stock_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import n11bootcamp_project_backend.common.dto.OrderItemQuantityResponse;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{orderId}/items")
    List<OrderItemQuantityResponse> getOrderItems(@PathVariable UUID orderId);
}