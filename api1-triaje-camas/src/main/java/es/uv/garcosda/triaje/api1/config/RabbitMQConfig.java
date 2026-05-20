/**
 * Configuración de RabbitMQ para API1.
 * Declara el topic exchange "urgencias", las colas de triaje por gravedad (leve, grave, critico)
 * y la cola del supervisor, junto con sus bindings y conversor JSON.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name.leve:q.pacientes.leve}")
    private String queueLeve;

    @Value("${rabbitmq.queue.name.grave:q.pacientes.grave}")
    private String queueGrave;

    @Value("${rabbitmq.queue.name.critico:q.pacientes.critico}")
    private String queueCritico;

    @Value("${rabbitmq.queue.name.supervisor:q.pacientes.supervisor}")
    private String queueSupervisor;

    @Value("${rabbitmq.exchange.name:urgencias}")
    private String exchange;

    @Value("${rabbitmq.routing.key.leve:pacientes.leve}")
    private String routingKeyLeve;

    @Value("${rabbitmq.routing.key.grave:pacientes.grave}")
    private String routingKeyGrave;

    @Value("${rabbitmq.routing.key.critico:pacientes.critico}")
    private String routingKeyCritico;

    @Value("${rabbitmq.routing.key.supervisor:pacientes.*}")
    private String routingKeySupervisor;

    @Bean
    public Queue queueLeve() {
        return new Queue(queueLeve, true, false, false);
    }

    @Bean
    public Queue queueGrave() {
        return new Queue(queueGrave, true, false, false);
    }

    @Bean
    public Queue queueCritico() {
        return new Queue(queueCritico, true, false, false);
    }

    @Bean
    public Queue queueSupervisor() {
        return new Queue(queueSupervisor, true, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding bindingLeve(Queue queueLeve, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueLeve).to(topicExchange).with(routingKeyLeve);
    }

    @Bean
    public Binding bindingGrave(Queue queueGrave, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueGrave).to(topicExchange).with(routingKeyGrave);
    }

    @Bean
    public Binding bindingCritico(Queue queueCritico, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueCritico).to(topicExchange).with(routingKeyCritico);
    }

    @Bean
    public Binding bindingSupervisor(Queue queueSupervisor, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueSupervisor).to(topicExchange).with(routingKeySupervisor);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}
