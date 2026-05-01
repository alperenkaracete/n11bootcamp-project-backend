package n11bootcamp_project_backend.stock_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.stock_service.dto.event.OrderEventMessage;
import n11bootcamp_project_backend.stock_service.dto.event.OrderItemMessage;
import n11bootcamp_project_backend.stock_service.service.StockService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final StockService stockService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "stock.order.queue")
    public void handleOrderCreated(OrderEventMessage orderEvent) {
        log.info("Order created event received: {}", orderEvent.orderId());

        // Her ürün için stok düş
        for (OrderItemMessage item : orderEvent.items()) {
            try {
                stockService.decreaseStock(
                        UUID.fromString(item.productId()),
                        item.quantity()
                );
                log.info("Stock decreased for productId: {}", item.productId());
            } catch (Exception e) {
                log.error("Stock decrease failed for productId: {}", item.productId());
                // Stok yetersizse payment.failed eventi yay
                rabbitTemplate.convertAndSend(
                        "saga.exchange",
                        "stock.failed",
                        orderEvent.orderId()
                );
                return;
            }
        }

        // Tüm stoklar başarılı → saga devam et
        rabbitTemplate.convertAndSend(
                "saga.exchange",
                "stock.reserved",
                orderEvent.orderId()
        );
    }
}