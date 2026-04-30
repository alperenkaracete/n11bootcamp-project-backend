package n11bootcamp_project_backend.payment_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange tanımla
    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange("saga.exchange");
    }

    // Logging exchange
    @Bean
    public TopicExchange loggingExchange() {
        return new TopicExchange("logging.exchange");
    }
}