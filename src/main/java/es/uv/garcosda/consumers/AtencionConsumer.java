package es.uv.garcosda.consumers;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.uv.garcosda.domain.Estado;
import es.uv.garcosda.domain.Paciente;
import es.uv.garcosda.services.PacienteService;

/**
 * Módulo de Atención (Consumidores).
 * Contiene 3 consumidores RabbitMQ, uno por cada grado de gravedad del paciente.
 * Cada consumidor escucha su cola correspondiente y simula el proceso de atención.
 */
@Component
public class AtencionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AtencionConsumer.class);
    private final Random random = new Random();

    @Autowired
    private PacienteService pacienteService;

    /**
     * Consumidor para pacientes con grado LEVE.
     * @param paciente el paciente recibido del mensaje RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queue.name.leve}")
    public void consumirLeve(Paciente paciente) {
        logger.info("[ATENCION-LEVE] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "LEVE");
    }

    /**
     * Consumidor para pacientes con grado GRAVE.
     * @param paciente el paciente recibido del mensaje RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queue.name.grave}")
    public void consumirGrave(Paciente paciente) {
        logger.info("[ATENCION-GRAVE] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "GRAVE");
    }

    /**
     * Consumidor para pacientes con grado CRITICO.
     * @param paciente el paciente recibido del mensaje RabbitMQ
     */
    @RabbitListener(queues = "${rabbitmq.queue.name.critico}")
    public void consumirCritico(Paciente paciente) {
        logger.info("[ATENCION-CRITICO] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "CRITICO");
    }

    /**
     * Método que simula el proceso de atención de un paciente.
     * @param paciente el paciente a atender
     * @param tipo etiqueta del tipo de atención para los logs
     */
    private void atenderPaciente(Paciente paciente, String tipo) {
        // Cambiar estado a CONSULTA en el servicio
        Paciente p = pacienteService.findByDni(paciente.getDni());
        if (p != null) {
            p.setEstado(Estado.CONSULTA);
            logger.info("[ATENCION-{}] Paciente {} en CONSULTA", tipo, paciente.getDni());
        }

        // Simular tiempo de consulta aleatorio entre 2 y 5 segundos
        int tiempoConsulta = 2000 + random.nextInt(3001);
        logger.info("[ATENCION-{}] Tiempo de consulta estimado: {} ms", tipo, tiempoConsulta);

        try {
            Thread.sleep(tiempoConsulta);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("[ATENCION-{}] Consulta interrumpida para paciente {}", tipo, paciente.getDni());
        }

        // Cambiar estado a ALTA tras la consulta
        if (p != null) {
            p.setEstado(Estado.ALTA);
            logger.info("[ATENCION-{}] Paciente {} dado de ALTA", tipo, paciente.getDni());
        }
    }
}
