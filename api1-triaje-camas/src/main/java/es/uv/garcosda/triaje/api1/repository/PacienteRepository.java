package es.uv.garcosda.triaje.api1.repository;

import es.uv.garcosda.triaje.api1.domain.Estado;
import es.uv.garcosda.triaje.api1.domain.Grado;
import es.uv.garcosda.triaje.api1.domain.Paciente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PacienteRepository extends ReactiveCrudRepository<Paciente, String> {
    Flux<Paciente> findByGrado(Grado grado);
    Flux<Paciente> findByEstado(Estado estado);
}
