package n11bootcamp_project_backend.order_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.order_service.enums.OrderStatus;
import n11bootcamp_project_backend.order_service.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = "order.payment.success.queue")
    public void handlePaymentSuccess(@Payload String orderId) {
        log.info("Payment success event received for orderId: {}", orderId);
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.CONFIRMED);
    }

    @RabbitListener(queues = "order.payment.failed.queue")
    public void handlePaymentFailed(@Payload String orderId) {
        log.info("Payment failed event received for orderId: {}", orderId);
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.CANCELLED);
    }

    @RabbitListener(queues = "order.stock.failed.queue")
    public void handleStockFailed(@Payload String orderId) {
        log.info("Stock failed event received for orderId: {}", orderId);
        orderService.updateOrderStatus(UUID.fromString(orderId), OrderStatus.CANCELLED);
    }
}