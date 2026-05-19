package es.uv.garcosda.sensores.api2.controller;

import es.uv.garcosda.sensores.api2.domain.LecturaVital;
import es.uv.garcosda.sensores.api2.service.LecturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/sensores")
public class SensorController {

    private final LecturaService lecturaService;

    public SensorController(LecturaService lecturaService) {
        this.lecturaService = lecturaService;
    }

    @PostMapping("/lecturas")
    public Mono<ResponseEntity<LecturaVital>> addLectura(@RequestBody LecturaVital lectura) {
        return lecturaService.guardar(lectura)
                .map(l -> ResponseEntity.status(HttpStatus.CREATED).body(l));
    }

    @GetMapping("/lecturas/{dni}/ultima")
    public Mono<ResponseEntity<LecturaVital>> getUltima(@PathVariable String dni) {
        return lecturaService.obtenerUltima(dni)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/lecturas/{dni}")
    public Mono<ResponseEntity<java.util.List<LecturaVital>>> getHistorico(
            @PathVariable String dni,
            @RequestParam Instant from,
            @RequestParam Instant to) {
        return lecturaService.obtenerHistorico(dni, from, to)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
