package n11bootcamp_project_backend.notification_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import n11bootcamp_project_backend.producer.LogProducer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentNotificationConsumer {

    private final JavaMailSender mailSender;
    private final LogProducer logProducer;

    @RabbitListener(queues = "notification.queue")
    public void handlePaymentSuccess(String orderId) {
        logProducer.sendLog("notification-service", "INFO", "Payment success event received for orderId: " + orderId);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("m.alperenk@gmail.com");
            message.setSubject("Siparişiniz Onaylandı!");
            message.setText("Merhaba, " + orderId + " numaralı siparişiniz onaylandı.");

            mailSender.send(message);
            logProducer.sendLog("notification-service", "INFO", "Notification email sent successfully for orderId: " + orderId);
        } catch (Exception e) {
            logProducer.sendLog("notification-service", "ERROR", "Failed to send notification email for orderId: " + orderId + ". Error: " + e.getMessage());
            throw e;
        }
    }
}