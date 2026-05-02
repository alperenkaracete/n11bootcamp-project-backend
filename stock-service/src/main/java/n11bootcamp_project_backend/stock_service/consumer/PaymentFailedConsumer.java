package n11bootcamp_project_backend.stock_service.consumer;

import lombok.RequiredArgsConstructor;
import n11bootcamp_project_backend.producer.LogProducer;
import n11bootcamp_project_backend.stock_service.client.OrderServiceClient;
import n11bootcamp_project_backend.stock_service.service.StockService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import n11bootcamp_project_backend.common.dto.OrderItemQuantityResponse;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final StockService stockService;
    private final OrderServiceClient orderServiceClient;
    private final LogProducer logProducer;

    @RabbitListener(queues = "stock.compensation.queue")
    public void handlePaymentFailed(String orderId) {
        logProducer.sendLog("stock-service", "WARN", "Payment failed, restoring stock for orderId: " + orderId);

        // Order-service'den sipariş detaylarını çek
        List<OrderItemQuantityResponse> items = orderServiceClient.getOrderItems(UUID.fromString(orderId));

        // Her ürünün stokunu geri yükle
        for (OrderItemQuantityResponse item : items) {
            stockService.increaseStock(item.getProductId(), item.getQuantity());
        }

        logProducer.sendLog("stock-service", "INFO", "Stock restored for orderId: " + orderId + ", Item count: " + items.size());
    }
}