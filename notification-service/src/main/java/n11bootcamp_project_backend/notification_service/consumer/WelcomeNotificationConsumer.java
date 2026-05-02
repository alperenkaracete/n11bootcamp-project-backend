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
public class WelcomeNotificationConsumer {

    private final JavaMailSender mailSender;
    private final LogProducer logProducer;

    @RabbitListener(queues = "welcome.queue")
    public void handleUserRegistration(String email) {
        logProducer.sendLog("notification-service", "INFO", "Welcome email event received for: " + email);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Hoş Geldiniz!");
            message.setText("N11 Bootcamp proje mağazasına hoş geldiniz! Kaydınız başarıyla tamamlandı.");

            mailSender.send(message);
            logProducer.sendLog("notification-service", "INFO", "Welcome email sent successfully to: " + email);
        } catch (Exception e) {
            logProducer.sendLog("notification-service", "ERROR", "Failed to send welcome email to: " + email + ". Error: " + e.getMessage());
            throw e;
        }
    }
}