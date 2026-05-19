package es.uv.garcosda.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el sistema de triaje de urgencias.
 * Define un Topic Exchange con 4 colas.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name.leve}")
    private String queueLeve;

    @Value("${rabbitmq.queue.name.grave}")
    private String queueGrave;

    @Value("${rabbitmq.queue.name.critico}")
    private String queueCritico;

    @Value("${rabbitmq.queue.name.supervisor}")
    private String queueSupervisor;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.leve}")
    private String routingKeyLeve;

    @Value("${rabbitmq.routing.key.grave}")
    private String routingKeyGrave;

    @Value("${rabbitmq.routing.key.critico}")
    private String routingKeyCritico;

    @Value("${rabbitmq.routing.key.supervisor}")
    private String routingKeySupervisor;

    @SuppressWarnings("unused")
    @Autowired
    private AmqpAdmin amqpAdmin;

    /**
     * Configura las colas, el exchange de tipo Topic y los bindings entre ellos.
     * Se declaran 4 colas durables y se enlazan al exchange con sus routing keys correspondientes.
     */
    @Bean
    public void Configure() {
        // Declarar las 4 colas
        Queue qLeve = new Queue(this.queueLeve, true, false, false);
        Queue qGrave = new Queue(this.queueGrave, true, false, false);
        Queue qCritico = new Queue(this.queueCritico, true, false, false);
        Queue qSupervisor = new Queue(this.queueSupervisor, true, false, false);

        amqpAdmin.declareQueue(qLeve);
        amqpAdmin.declareQueue(qGrave);
        amqpAdmin.declareQueue(qCritico);
        amqpAdmin.declareQueue(qSupervisor);

        // Declarar el exchange de tipo Topic
        TopicExchange e = new TopicExchange(this.exchange);
        amqpAdmin.declareExchange(e);

        // Enlazar cada cola al exchange con su routing key correspondiente
        amqpAdmin.declareBinding(BindingBuilder
                .bind(qLeve)
                .to(e)
                .with(routingKeyLeve));

        amqpAdmin.declareBinding(BindingBuilder
                .bind(qGrave)
                .to(e)
                .with(routingKeyGrave));

        amqpAdmin.declareBinding(BindingBuilder
                .bind(qCritico)
                .to(e)
                .with(routingKeyCritico));

        amqpAdmin.declareBinding(BindingBuilder
                .bind(qSupervisor)
                .to(e)
                .with(routingKeySupervisor));
    }

    /**
     * Conversor de mensajes a formato JSON usando Jackson.
     * @return el conversor Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
