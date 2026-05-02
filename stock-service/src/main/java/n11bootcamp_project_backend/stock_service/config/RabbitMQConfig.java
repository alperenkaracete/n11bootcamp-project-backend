package n11bootcamp_project_backend.stock_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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

    // order.created eventini dinleyecek queue
    @Bean
    public Queue stockOrderQueue() {
        return new Queue("stock.order.queue", true);
    }

    @Bean
    public Binding stockOrderBinding(Queue stockOrderQueue,
                                     TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(stockOrderQueue)
                .to(sagaExchange)
                .with("order.created");
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
    public Queue stockCompensationQueue() {
        return new Queue("stock.compensation.queue", true);
    }

    @Bean
    public Binding stockCompensationBinding(Queue stockCompensationQueue, TopicExchange sagaExchange) {
        return BindingBuilder.bind(stockCompensationQueue).to(sagaExchange).with("payment.failed");
    }
}