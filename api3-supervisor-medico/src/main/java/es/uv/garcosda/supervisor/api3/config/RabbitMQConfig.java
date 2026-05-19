package es.uv.garcosda.supervisor.api3.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name.supervisor:q.pacientes.supervisor}")
    private String queueSupervisor;

    @Value("${rabbitmq.exchange.name:urgencias}")
    private String exchange;

    @Value("${rabbitmq.routing.key.supervisor:pacientes.*}")
    private String routingKeySupervisor;

    @Bean
    public Queue queueSupervisor() {
        return new Queue(queueSupervisor, true, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding bindingSupervisor(Queue queueSupervisor, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueSupervisor).to(topicExchange).with(routingKeySupervisor);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
