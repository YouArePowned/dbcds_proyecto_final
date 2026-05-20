/**
 * Controlador REST reactivo del Supervisor Médico.
 * Agrega datos de API1 y API2 para ofrecer vistas completas del estado del hospital.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.supervisor.api3.controller;

import es.uv.garcosda.supervisor.api3.service.SupervisorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/supervisor")
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @GetMapping("/pacientes/{dni}/estado-completo")
    public Mono<ResponseEntity<Map<String, Object>>> getEstadoCompleto(@PathVariable String dni) {
        return supervisorService.obtenerEstadoCompleto(dni)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/camas/cercanas")
    public Mono<ResponseEntity<List<Map<String, Object>>>> getCamasCercanas(
            @RequestParam double lat, @RequestParam double lon) {
        return supervisorService.obtenerCamasCercanas(lat, lon)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/resumen")
    public Mono<ResponseEntity<Map<String, Object>>> getResumen() {
        return supervisorService.obtenerResumen()
                .map(ResponseEntity::ok);
    }
}
