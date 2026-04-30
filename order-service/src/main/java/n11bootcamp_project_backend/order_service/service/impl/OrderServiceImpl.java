package n11bootcamp_project_backend.order_service.service.impl;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.order_service.dto.request.CreateOrderRequest;
import n11bootcamp_project_backend.order_service.dto.response.OrderItemResponse;
import n11bootcamp_project_backend.order_service.dto.response.OrderResponse;
import n11bootcamp_project_backend.order_service.entity.Order;
import n11bootcamp_project_backend.order_service.entity.OrderItem;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;
import n11bootcamp_project_backend.order_service.repository.OrderRepository;
import n11bootcamp_project_backend.order_service.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        // OrderItem listesi oluştur
        List<OrderItem> orderItems = request.items().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.productId())
                        .productName(item.productName())
                        .price(item.price())
                        .quantity(item.quantity())
                        .build())
                .toList();

        // Siparişi oluştur
        Order order = Order.builder()
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .totalPrice(request.totalPrice())
                .items(orderItems)
                .build();

        // OrderItem'lara order referansını set et
        orderItems.forEach(item -> item.setOrder(order));

        // DB'ye kaydet
        Order savedOrder = orderRepository.save(order);

        // RabbitMQ'ya event yay — stock ve payment servisler dinliyor
        rabbitTemplate.convertAndSend(
                "saga.exchange",
                "order.created",
                savedOrder.getId().toString()
        );

        return toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    // Entity → Response dönüşümü
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}