package n11bootcamp_project_backend.payment_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange("saga.exchange");
    }

    @Bean
    public TopicExchange loggingExchange() {
        return new TopicExchange("logging.exchange");
    }

    // Stock reserved olunca payment'ı tetikleyecek queue
    @Bean
    public Queue paymentProcessQueue() {
        return new Queue("payment.process.queue", true);
    }

    // stock.reserved routing key'ine bağla
    @Bean
    public Binding paymentProcessBinding(Queue paymentProcessQueue,
                                         TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(paymentProcessQueue)
                .to(sagaExchange)
                .with("stock.reserved");
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}