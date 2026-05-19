package es.uv.garcosda.triaje.api1.consumers;

import es.uv.garcosda.triaje.api1.domain.Paciente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PacienteEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PacienteEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public PacienteEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Void> emitirEventoPaciente(Paciente paciente) {
        return Mono.fromRunnable(() -> {
            String routingKey = "pacientes." + paciente.getGrado().name().toLowerCase();
            rabbitTemplate.convertAndSend("urgencias", routingKey, paciente);
            logger.info("Evento RabbitMQ emitido: {} -> {}", routingKey, paciente.getDni());
        });
    }
}
