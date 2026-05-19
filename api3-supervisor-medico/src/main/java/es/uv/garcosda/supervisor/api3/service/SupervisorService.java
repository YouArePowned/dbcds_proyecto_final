package es.uv.garcosda.supervisor.api3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupervisorService {

    private static final Logger logger = LoggerFactory.getLogger(SupervisorService.class);

    private final WebClient webClient;

    public SupervisorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> obtenerEstadoCompleto(String dni) {
        Mono<Map> pacienteMono = webClient.get()
                .uri("http://api1-triaje-camas/api/v1/triaje/pacientes/" + dni)
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    logger.error("Error obteniendo paciente {}: {}", dni, e.getMessage());
                    return Mono.just(Map.of("error", "Paciente no disponible"));
                });

        Mono<Map> lecturaMono = webClient.get()
                .uri("http://api2-sensores-vitales/api/v1/sensores/lecturas/" + dni + "/ultima")
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    logger.error("Error obteniendo lectura {}: {}", dni, e.getMessage());
                    return Mono.just(Map.of("error", "Lectura no disponible"));
                });

        return Mono.zip(pacienteMono, lecturaMono)
                .map(tuple -> Map.of(
                        "paciente", (Object) tuple.getT1(),
                        "ultimaLectura", (Object) tuple.getT2()
                ));
    }

    @SuppressWarnings("unchecked")
    public Mono<Map<String, Object>> obtenerResumen() {
        Mono<List> pacientesMono = webClient.get()
                .uri("http://api1-triaje-camas/api/v1/triaje/pacientes")
                .retrieve()
                .bodyToMono(List.class)
                .onErrorResume(e -> {
                    logger.error("Error obteniendo pacientes: {}", e.getMessage());
                    return Mono.just(Collections.emptyList());
                });

        Mono<List> camasMono = webClient.get()
                .uri("http://api1-triaje-camas/api/v1/triaje/camas")
                .retrieve()
                .bodyToMono(List.class)
                .onErrorResume(e -> {
                    logger.error("Error obteniendo camas: {}", e.getMessage());
                    return Mono.just(Collections.emptyList());
                });

        return Mono.zip(pacientesMono, camasMono)
                .map(tuple -> {
                    List<Map<String, Object>> pacientes = tuple.getT1();
                    List<Map<String, Object>> camas = tuple.getT2();

                    long espera = pacientes.stream()
                            .filter(p -> "ESPERA".equals(p.get("estado")))
                            .count();
                    long consulta = pacientes.stream()
                            .filter(p -> "CONSULTA".equals(p.get("estado")))
                            .count();
                    long alta = pacientes.stream()
                            .filter(p -> "ALTA".equals(p.get("estado")))
                            .count();
                    long camasOcupadas = camas.stream()
                            .filter(c -> Boolean.TRUE.equals(c.get("ocupada")))
                            .count();
                    long camasDisponibles = camas.size() - camasOcupadas;

                    Map<String, Object> resumen = new HashMap<>();
                    resumen.put("totalPacientes", pacientes.size());
                    resumen.put("pacientesEspera", espera);
                    resumen.put("pacientesConsulta", consulta);
                    resumen.put("pacientesAlta", alta);
                    resumen.put("camasDisponibles", camasDisponibles);
                    resumen.put("camasOcupadas", camasOcupadas);
                    return resumen;
                });
    }

    @SuppressWarnings("unchecked")
    public Mono<List<Map<String, Object>>> obtenerCamasCercanas(double lat, double lon) {
        return webClient.get()
                .uri("http://api1-triaje-camas/api/v1/triaje/camas/disponibles")
                .retrieve()
                .bodyToMono(List.class)
                .map(camas -> {
                    List<Map<String, Object>> lista = (List<Map<String, Object>>) camas;
                    return lista.stream()
                            .map(c -> {
                                double cLat = toDouble(c.get("latitud"));
                                double cLon = toDouble(c.get("longitud"));
                                double dist = distancia(lat, lon, cLat, cLon);
                                Map<String, Object> result = new HashMap<>(c);
                                result.put("distanciaKm", Math.round(dist * 100.0) / 100.0);
                                return result;
                            })
                            .sorted(Comparator.comparingDouble(m -> (Double) m.get("distanciaKm")))
                            .collect(Collectors.toList());
                })
                .onErrorResume(e -> {
                    logger.error("Error obteniendo camas cercanas: {}", e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    private double toDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        return 0.0;
    }

    public double distancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
