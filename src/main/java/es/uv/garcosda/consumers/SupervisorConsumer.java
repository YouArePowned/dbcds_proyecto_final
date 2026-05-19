package es.uv.garcosda.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import es.uv.garcosda.domain.Paciente;

/**
 * Módulo Supervisor (Consumidor).
 * Consumidor RabbitMQ que actúa como médico supervisor.
 * Su función es registrar la entrada de todos los pacientes para supervisión.
 */
@Component
public class SupervisorConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SupervisorConsumer.class);

    /**
     * Consumidor del médico supervisor.
     * Recibe todos los mensajes de pacientes a través de la cola que
     * está enlazada al exchange con el patrón "pacientes.*" del Topic Exchange.
     * @param paciente el paciente recibido del mensaje RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queue.name.supervisor}")
    public void supervisar(Paciente paciente) {
        logger.info("=============================================");
        logger.info("[SUPERVISOR] Nuevo paciente registrado en urgencias");
        logger.info("[SUPERVISOR] DNI: {}", paciente.getDni());
        logger.info("[SUPERVISOR] Nombre: {} {}", paciente.getNombre(), paciente.getApellidos());
        logger.info("[SUPERVISOR] Género: {}", paciente.getGenero());
        logger.info("[SUPERVISOR] Grado: {}", paciente.getGrado());
        logger.info("[SUPERVISOR] Estado: {}", paciente.getEstado());
        logger.info("=============================================");
    }
}
