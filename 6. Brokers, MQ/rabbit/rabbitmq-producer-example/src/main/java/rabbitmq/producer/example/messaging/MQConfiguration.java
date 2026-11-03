package rabbitmq.producer.example.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfiguration {

    @Value("${myqueue}")
    String queue;

    @Value("${myqueue2}")
    String queue2;

    @Value("${myexchange}")
    String exchange;

    @Value("${routingkey}")
    String routingkey;

    @Bean
    Queue queue() {
        return new Queue(queue, true);
    }

    @Bean
    Queue queue2() {
        return new Queue(queue2, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    Binding binding(Queue queue2, DirectExchange exchange) {
        return BindingBuilder.bind(queue2).to(exchange).with(routingkey);
    }

    /*
     * Registrujemo bean koji ce sluziti za konekciju na RabbitMQ gde se mi u
     * primeru kacimo u lokalu.
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory("localhost");
    }
}
