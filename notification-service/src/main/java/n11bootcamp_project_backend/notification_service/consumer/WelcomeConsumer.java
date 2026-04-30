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
public class WelcomeConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = "welcome.queue")
    public void handleUserRegistered(String email) {
        log.info("Başarı ile kayıt olundu!");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Hoş Geldiniz!");
        message.setText("Merhaba, aramıza hoş geldiniz!");
        mailSender.send(message);
    }
}
