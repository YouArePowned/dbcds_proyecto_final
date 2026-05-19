package es.uv.garcosda.triaje.api1.repository;

import es.uv.garcosda.triaje.api1.domain.Planta;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PlantaRepository extends ReactiveCrudRepository<Planta, Long> {
}
