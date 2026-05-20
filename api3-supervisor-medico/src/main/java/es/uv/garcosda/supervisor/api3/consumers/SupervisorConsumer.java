/**
 * Consumidor RabbitMQ del Supervisor Médico.
 * Escucha la cola del supervisor (wildcard sobre todas las gravedades) y registra
 * en logs la llegada de nuevos pacientes al sistema de triaje.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.supervisor.api3.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SupervisorConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SupervisorConsumer.class);

    @RabbitListener(queues = "${rabbitmq.queue.name.supervisor:q.pacientes.supervisor}")
    public void supervisar(Map<String, Object> paciente) {
        logger.info("=============================================");
        logger.info("[SUPERVISOR] Nuevo paciente en urgencias");
        logger.info("[SUPERVISOR] DNI: {}", paciente.get("dni"));
        logger.info("[SUPERVISOR] Nombre: {} {}", paciente.get("nombre"), paciente.get("apellidos"));
        logger.info("[SUPERVISOR] Grado: {}", paciente.get("grado"));
        logger.info("[SUPERVISOR] Estado: {}", paciente.get("estado"));
        logger.info("=============================================");
    }
}
