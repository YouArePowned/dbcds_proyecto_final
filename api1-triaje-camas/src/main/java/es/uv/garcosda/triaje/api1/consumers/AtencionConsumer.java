package es.uv.garcosda.triaje.api1.consumers;

import es.uv.garcosda.triaje.api1.domain.Estado;
import es.uv.garcosda.triaje.api1.domain.Paciente;
import es.uv.garcosda.triaje.api1.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AtencionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AtencionConsumer.class);
    private final Random random = new Random();

    private final PacienteRepository pacienteRepository;
    private final es.uv.garcosda.triaje.api1.repository.CamaRepository camaRepository;

    public AtencionConsumer(PacienteRepository pacienteRepository, es.uv.garcosda.triaje.api1.repository.CamaRepository camaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.camaRepository = camaRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name.leve}")
    public void consumirLeve(Paciente paciente) {
        logger.info("[ATENCION-LEVE] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "LEVE", 60000, 90000); // 1.0 min a 1.5 min
    }

    @RabbitListener(queues = "${rabbitmq.queue.name.grave}")
    public void consumirGrave(Paciente paciente) {
        logger.info("[ATENCION-GRAVE] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "GRAVE", 90000, 130000); // 1.5 min a 2.1 min
    }

    @RabbitListener(queues = "${rabbitmq.queue.name.critico}")
    public void consumirCritico(Paciente paciente) {
        logger.info("[ATENCION-CRITICO] Recibido paciente: {} - {}", paciente.getDni(), paciente.getNombre());
        atenderPaciente(paciente, "CRITICO", 130000, 180000); // 2.1 min a 3.0 min
    }

    private void atenderPaciente(Paciente paciente, String tipo, int minMs, int maxMs) {
        pacienteRepository.findById(paciente.getDni())
                .flatMap(p -> {
                    p.setEstado(Estado.CONSULTA);
                    return pacienteRepository.save(p).doOnSuccess(saved ->
                        logger.info("[ATENCION-{}] Paciente {} en CONSULTA", tipo, paciente.getDni())
                    );
                })
                .subscribe();

        int tiempo = minMs + random.nextInt(maxMs - minMs);
        logger.info("[ATENCION-{}] Tiempo estimado: {} ms", tipo, tiempo);

        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("[ATENCION-{}] Consulta interrumpida", tipo);
            return;
        }

        pacienteRepository.findById(paciente.getDni())
                .flatMap(p -> {
                    p.setEstado(Estado.ALTA);
                    reactor.core.publisher.Mono<es.uv.garcosda.triaje.api1.domain.Cama> freeBed = (p.getCamaId() != null)
                        ? camaRepository.findById(p.getCamaId()).flatMap(c -> { c.setOcupada(false); return camaRepository.save(c); })
                        : reactor.core.publisher.Mono.empty();
                    
                    return freeBed.then(pacienteRepository.save(p)).doOnSuccess(saved -> 
                        logger.info("[ATENCION-{}] Paciente {} dado de ALTA y cama liberada", tipo, p.getDni())
                    );
                })
                .subscribe();
    }
}
