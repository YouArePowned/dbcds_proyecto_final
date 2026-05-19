package es.uv.garcosda.sensores.api2.service;

import es.uv.garcosda.sensores.api2.domain.LecturaVital;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class LecturaService {

    private final Map<String, List<LecturaVital>> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        String[] dnies = {"12345678A", "23456789B", "34567890C", "67890123F"};
        for (String dni : dnies) {
            List<LecturaVital> lecturas = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                lecturas.add(generarLectura(dni, Instant.now().minusSeconds(i * 300)));
            }
            store.put(dni, lecturas);
        }
    }

    public Mono<LecturaVital> guardar(LecturaVital lectura) {
        lectura.setId(UUID.randomUUID().toString());
        if (lectura.getTimestamp() == null) {
            lectura.setTimestamp(Instant.now());
        }
        store.computeIfAbsent(lectura.getPacienteDni(), k -> new ArrayList<>()).add(lectura);
        return Mono.just(lectura);
    }

    public Mono<LecturaVital> obtenerUltima(String dni) {
        List<LecturaVital> lista = store.get(dni);
        if (lista == null || lista.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(lista.get(lista.size() - 1));
    }

    public Flux<LecturaVital> obtenerHistorico(String dni, Instant from, Instant to) {
        List<LecturaVital> lista = store.getOrDefault(dni, Collections.emptyList());
        return Flux.fromStream(lista.stream()
                .filter(l -> !l.getTimestamp().isBefore(from) && !l.getTimestamp().isAfter(to)));
    }

    private LecturaVital generarLectura(String dni, Instant timestamp) {
        return new LecturaVital(
                UUID.randomUUID().toString(),
                dni,
                timestamp,
                60 + random.nextInt(60),
                92 + random.nextInt(8),
                (110 + random.nextInt(30)) + "/" + (70 + random.nextInt(20)),
                35 + random.nextInt(11)
        );
    }
}
