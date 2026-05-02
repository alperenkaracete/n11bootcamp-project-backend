package n11bootcamp_project_backend.order_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.order_service.dto.event.OrderEventMessage;
import n11bootcamp_project_backend.order_service.dto.event.OrderItemMessage;
import n11bootcamp_project_backend.order_service.dto.request.CreateOrderRequest;
import n11bootcamp_project_backend.order_service.dto.request.PaymentRequest;
import n11bootcamp_project_backend.order_service.dto.response.OrderItemResponse;
import n11bootcamp_project_backend.order_service.dto.response.OrderResponse;
import n11bootcamp_project_backend.order_service.entity.Order;
import n11bootcamp_project_backend.order_service.entity.OrderItem;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;
import n11bootcamp_project_backend.order_service.repository.OrderRepository;
import n11bootcamp_project_backend.order_service.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import n11bootcamp_project_backend.producer.LogProducer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j // Loglama için eklendi
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final LogProducer logProducer;

    @Override
    @Transactional // Veritabanı bütünlüğü için eklendi
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
        log.info("Order created with status PENDING. OrderId: {}", savedOrder.getId());
        logProducer.sendLog("order-service", "INFO", "Order created with status PENDING. OrderId: " + order.getId());

        // --- YENİ EKLENEN REDIS KISMI ---
        // Kredi kartı bilgilerini Payment Service'in alabilmesi için Redis'e atıyoruz.
        // Veri güvenliği için 15 dakika sonra otomatik silinecek (TTL).
        PaymentRequest paymentRequest = new PaymentRequest(
                savedOrder.getId(),
                request.userId(),
                request.totalPrice(),
                request.cardHolderName(),
                request.cardNumber(),
                request.expireMonth(),
                request.expireYear(),
                request.cvc()
        );

        String redisKey = "payment:" + savedOrder.getId();
        try {
            String jsonValue = objectMapper.writeValueAsString(paymentRequest);
            redisTemplate.opsForValue().set(redisKey, jsonValue, 15, TimeUnit.MINUTES);
            log.info("Payment details saved to Redis with key: {}", redisKey);
            logProducer.sendLog("order-service", "INFO", "Payment details saved to Redis with key. ");
        } catch (Exception e) {
            log.error("Redis serialization error", e);
            logProducer.sendLog("order-service", "ERROR", "Redis serialization error");
        }

        // RabbitMQ'ya event yay — Sadece stock servisi dinleyecek
        List<OrderItemMessage> itemMessages = savedOrder.getItems().stream()
                .map(item -> new OrderItemMessage(
                        item.getProductId().toString(),
                        item.getQuantity()
                ))
                .toList();

        OrderEventMessage eventMessage = new OrderEventMessage(
                savedOrder.getId().toString(),
                itemMessages
        );

        rabbitTemplate.convertAndSend(
                "saga.exchange",
                "order.created",
                eventMessage
        );
        log.info("Order created event sent to Stock Service for OrderId: {}", savedOrder.getId());
        logProducer.sendLog("order-service", "INFO", "Order created event sent to Stock Service for OrderId: " + savedOrder.getId());

        return toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logProducer.sendLog("order-service", "ERROR", "Order not found: " + orderId);
                    return new RuntimeException("Order could not found");
                });
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
        log.info("Order status updated to {} for OrderId: {}", status, orderId);
        logProducer.sendLog("order-service", "INFO", "Order status updated to: " + status + "for OrderId: " + orderId);
    }

    @Override
    public List<OrderItemResponse> getOrderItems(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logProducer.sendLog("order-service", "ERROR", "Order not found for items query: " + orderId);
                    return new RuntimeException("Order not found");
                });

        return order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .toList();
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