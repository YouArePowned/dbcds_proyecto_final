package es.uv.garcosda.triaje.api1.repository;

import es.uv.garcosda.triaje.api1.domain.Cama;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CamaRepository extends ReactiveCrudRepository<Cama, Long> {
    Flux<Cama> findByOcupada(Boolean ocupada);
    Mono<Cama> findByCodigo(String codigo);
}
