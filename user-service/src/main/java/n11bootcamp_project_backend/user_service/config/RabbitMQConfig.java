package n11bootcamp_project_backend.user_service.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange("saga.exchange");
    }
}