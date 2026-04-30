package n11bootcamp_project_backend.log_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange loggingExchange() {
        return new TopicExchange("logging.exchange");
    }

    @Bean
    public Queue logQueue() {
        return new Queue("log.queue", true);
    }

    @Bean
    public Binding logBinding(Queue logQueue, TopicExchange loggingExchange) {
        return BindingBuilder
                .bind(logQueue)
                .to(loggingExchange)
                .with("log.event");
    }
}