package n11bootcamp_project_backend.payment_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.payment_service.service.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final PaymentService paymentService;

    // Kuyruk adının kendi projenle uyuştuğundan emin ol
    @RabbitListener(queues = "payment.process.queue")
    public void handleStockReserved(@Payload String orderIdString) {
        log.info("Stock reserved event received, initiating payment process for orderId: {}", orderIdString);

        try {
            // Gelen String'i UUID'ye çeviriyoruz
            UUID orderId = UUID.fromString(orderIdString);

            // Senin PaymentService'deki metodunu doğrudan orderId ile çağırıyoruz
            paymentService.processPayment(orderId);

        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format received from queue: {}", orderIdString, e);
        } catch (Exception e) {
            // Iyzico hatası veya Redis'ten veri bulunamaması gibi durumlar buraya düşer
            log.error("Payment processing failed for orderId: {}", orderIdString, e);
        }
    }
}