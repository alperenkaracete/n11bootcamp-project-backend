package n11bootcamp_project_backend.producer;

import n11bootcamp_project_backend.common.constants.RabbitConstants;
import n11bootcamp_project_backend.common.dto.LogMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LogProducer {

    private final RabbitTemplate rabbitTemplate;

    // Circular dependency'yi kırmak için lazy injection
    public LogProducer(@Lazy RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendLog(String serviceName, String level, String message) {
        LogMessage logMessage = LogMessage.builder()
                .serviceName(serviceName)
                .level(level)
                .message(message)
                .build();

        rabbitTemplate.convertAndSend(
                RabbitConstants.LOG_EXCHANGE,
                RabbitConstants.LOG_ROUTING_KEY,
                logMessage
        );
    }
}