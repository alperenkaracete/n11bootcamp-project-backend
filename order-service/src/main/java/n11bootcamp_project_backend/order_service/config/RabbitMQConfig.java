package n11bootcamp_project_backend.order_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange("saga.exchange");
    }

    @Bean
    public TopicExchange loggingExchange() {
        return new TopicExchange("logging.exchange");
    }

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue("order.payment.success.queue", true);
    }

    @Bean
    public Binding paymentSuccessBinding(Queue paymentSuccessQueue,
                                         TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(paymentSuccessQueue)
                .to(sagaExchange)
                .with("payment.success");
    }

    @Bean
    public Queue paymentFailedQueue() {
        return new Queue("order.payment.failed.queue", true);
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue,
                                        TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(paymentFailedQueue)
                .to(sagaExchange)
                .with("payment.failed");
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

    @Bean
    public Queue stockFailedQueue() {
        return new Queue("order.stock.failed.queue", true);
    }

    @Bean
    public Binding stockFailedBinding(Queue stockFailedQueue,
                                      TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(stockFailedQueue)
                .to(sagaExchange)
                .with("stock.failed");
    }
}