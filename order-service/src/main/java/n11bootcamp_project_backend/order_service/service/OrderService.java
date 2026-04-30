package n11bootcamp_project_backend.order_service.service;

import n11bootcamp_project_backend.order_service.dto.request.CreateOrderRequest;
import n11bootcamp_project_backend.order_service.dto.response.OrderResponse;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    // Sipariş oluştur
    OrderResponse createOrder(CreateOrderRequest request);

    // Sipariş getir
    OrderResponse getOrderById(UUID orderId);

    // Kullanıcının tüm siparişleri
    List<OrderResponse> getOrdersByUserId(UUID userId);

    // Sipariş durumunu güncelle (Saga'dan tetiklenir)
    void updateOrderStatus(UUID orderId, OrderStatus status);
}