package n11bootcamp_project_backend.notification_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "notification.queue")
    public void handlePaymentSuccess(String orderId) {
        log.info("Payment success event received for orderId: {}", orderId);

        // Mail gönder
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("m.alperenk@gmail.com"); // ileride User Service'ten gelecek
        message.setSubject("Siparişiniz Onaylandı!");
        message.setText("Merhaba, " + orderId + " numaralı siparişiniz onaylandı.");

        mailSender.send(message);
        log.info("Notification email sent for orderId: {}", orderId);
    }
}