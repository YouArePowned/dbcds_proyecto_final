/**
 * Repositorio reactivo para la entidad Cama.
 * Extiende ReactiveCrudRepository para operaciones CRUD no bloqueantes sobre H2.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.repository;

import es.uv.garcosda.triaje.api1.domain.Cama;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CamaRepository extends ReactiveCrudRepository<Cama, Long> {
    Flux<Cama> findByOcupada(Boolean ocupada);
}
