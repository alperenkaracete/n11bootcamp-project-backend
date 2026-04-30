package n11bootcamp_project_backend.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.order_service.dto.request.CreateOrderRequest;
import n11bootcamp_project_backend.order_service.dto.response.OrderResponse;
import n11bootcamp_project_backend.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Sipariş oluştur
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request));
    }

    // Sipariş getir
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // Kullanıcının tüm siparişleri
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
}