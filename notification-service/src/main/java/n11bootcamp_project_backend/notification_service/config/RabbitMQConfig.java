package n11bootcamp_project_backend.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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

    // payment.success eventini dinleyecek queue
    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.queue", true);
    }

    // Queue'yu exchange'e bağla
    @Bean
    public Binding notificationBinding(Queue notificationQueue,
                                       TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(sagaExchange)
                .with("payment.success");
    }

    @Bean
    public Queue welcomeQueue() {
        return new Queue("welcome.queue", true);
    }

    @Bean
    public Binding welcomeBinding(Queue welcomeQueue, TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(welcomeQueue)
                .to(sagaExchange)
                .with("user.registered");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}