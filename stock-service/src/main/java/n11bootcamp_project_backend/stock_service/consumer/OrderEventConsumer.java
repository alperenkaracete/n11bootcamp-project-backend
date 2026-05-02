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

        try {
            // Tüm stok düşme işlemini tek bir işlem (transaction) olarak servise devret
            stockService.decreaseStocksForOrder(orderEvent.items());

            // Tüm stoklar BAŞARILI düştüyse saga'ya devam et
            rabbitTemplate.convertAndSend("saga.exchange", "stock.reserved", orderEvent.orderId());
            log.info("Stock reserved for orderId: {}", orderEvent.orderId());

        } catch (Exception e) {
            log.error("Stock reservation failed for orderId: {}. Reason: {}", orderEvent.orderId(), e.getMessage());

            // Stok YETERSİZSE saga'yı iptal et
            rabbitTemplate.convertAndSend("saga.exchange", "stock.failed", orderEvent.orderId());
        }
    }
}