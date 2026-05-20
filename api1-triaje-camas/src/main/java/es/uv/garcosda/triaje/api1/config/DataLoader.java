/**
 * Carga datos iniciales de demo en H2 in-memory al arrancar API1.
 * Limpia tablas previas, inserta plantas, camas y pacientes de prueba.
 * Reinicia el autoincrement de camas a 100 para evitar DuplicateKeyException.
 * Los eventos RabbitMQ de los pacientes demo están comentados para que inicien en ESPERA
 * y puedan ser admitidos manualmente desde el dashboard.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.config;

import es.uv.garcosda.triaje.api1.domain.*;
import es.uv.garcosda.triaje.api1.repository.CamaRepository;
import es.uv.garcosda.triaje.api1.repository.PacienteRepository;
import es.uv.garcosda.triaje.api1.repository.PlantaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
public class DataLoader {

    @Bean
    @DependsOn("initializer")
    CommandLineRunner initData(PlantaRepository plantaRepo, CamaRepository camaRepo,
                               PacienteRepository pacienteRepo, R2dbcEntityTemplate template,
                               es.uv.garcosda.triaje.api1.consumers.PacienteEventProducer eventProducer) {
        return args -> {
            template.getDatabaseClient().sql("DELETE FROM pacientes").then().block();
            template.getDatabaseClient().sql("DELETE FROM camas").then().block();
            template.getDatabaseClient().sql("DELETE FROM plantas").then().block();

            Planta urgencias = new Planta(1L, "Urgencias", 39.4699, -0.3763);
            Planta reanimacion = new Planta(2L, "Reanimacion", 39.4705, -0.3770);
            Planta observacion = new Planta(3L, "Observacion", 39.4685, -0.3755);

            template.insert(Planta.class).using(urgencias).block();
            template.insert(Planta.class).using(reanimacion).block();
            template.insert(Planta.class).using(observacion).block();

            Cama c1 = new Cama(null, "C-URG-01", 1L, false, 39.4699, -0.3763);
            Cama c2 = new Cama(null, "C-URG-02", 1L, true, 39.4700, -0.3764);
            Cama c3 = new Cama(null, "C-REA-01", 2L, true, 39.4705, -0.3770);
            Cama c4 = new Cama(null, "C-OBS-01", 3L, false, 39.4685, -0.3755);

            template.insert(Cama.class).using(c1).block();
            template.insert(Cama.class).using(c2).block();
            template.insert(Cama.class).using(c3).block();
            template.insert(Cama.class).using(c4).block();

            Paciente p1 = new Paciente("12345678A", "Juan", "Garcia Lopez", "M", Grado.LEVE, Estado.ESPERA, null);
            Paciente p2 = new Paciente("23456789B", "Maria", "Fernandez Ruiz", "F", Grado.GRAVE, Estado.ESPERA, 2L);
            Paciente p3 = new Paciente("34567890C", "Pedro", "Martinez Sanchez", "M", Grado.CRITICO, Estado.ESPERA, null);
            Paciente p4 = new Paciente("45678901D", "Ana", "Lopez Diaz", "F", Grado.LEVE, Estado.ESPERA, null);
            Paciente p5 = new Paciente("56789012E", "Carlos", "Rodriguez Perez", "M", Grado.GRAVE, Estado.ESPERA, null);
            Paciente p6 = new Paciente("67890123F", "Laura", "Sanchez Ruiz", "F", Grado.CRITICO, Estado.ESPERA, 3L);

            template.insert(Paciente.class).using(p1).block();
            template.insert(Paciente.class).using(p2).block();
            template.insert(Paciente.class).using(p3).block();
            template.insert(Paciente.class).using(p4).block();
            template.insert(Paciente.class).using(p5).block();
            template.insert(Paciente.class).using(p6).block();

            // Restart auto-increment identity sequence for beds to prevent DuplicateKeyExceptions
            template.getDatabaseClient().sql("ALTER TABLE camas ALTER COLUMN id RESTART WITH 100").then().block();

            // Commented out so pre-seeded patients stay in ESPERA and can be manually admitted/processed in the Web UI
            // eventProducer.emitirEventoPaciente(p1).block();
            // eventProducer.emitirEventoPaciente(p2).block();
            // eventProducer.emitirEventoPaciente(p3).block();
            // eventProducer.emitirEventoPaciente(p4).block();
            // eventProducer.emitirEventoPaciente(p5).block();
            // eventProducer.emitirEventoPaciente(p6).block();
        };
    }
}
