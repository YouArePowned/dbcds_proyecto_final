package es.uv.garcosda.triaje.api1.controller;

import es.uv.garcosda.triaje.api1.consumers.PacienteEventProducer;
import es.uv.garcosda.triaje.api1.domain.*;
import es.uv.garcosda.triaje.api1.repository.CamaRepository;
import es.uv.garcosda.triaje.api1.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/triaje")
public class TriajeController {

    private final PacienteRepository pacienteRepo;
    private final CamaRepository camaRepo;
    private final PacienteEventProducer eventProducer;
    private final org.springframework.data.r2dbc.core.R2dbcEntityTemplate entityTemplate;

    public TriajeController(PacienteRepository pacienteRepo, CamaRepository camaRepo, PacienteEventProducer eventProducer, org.springframework.data.r2dbc.core.R2dbcEntityTemplate entityTemplate) {
        this.pacienteRepo = pacienteRepo;
        this.camaRepo = camaRepo;
        this.eventProducer = eventProducer;
        this.entityTemplate = entityTemplate;
    }

    @GetMapping("/pacientes")
    public Flux<org.springframework.hateoas.EntityModel<Paciente>> getPacientes() {
        return pacienteRepo.findAll()
                .map(p -> {
                    org.springframework.hateoas.EntityModel<Paciente> em = org.springframework.hateoas.EntityModel.of(p);
                    em.add(org.springframework.hateoas.Link.of("/api/v1/triaje/pacientes/" + p.getDni()).withSelfRel());
                    em.add(org.springframework.hateoas.Link.of("/api/v1/sensores/lecturas/" + p.getDni()).withRel("historial_clinico"));
                    if (p.getCamaId() != null) {
                        em.add(org.springframework.hateoas.Link.of("/api/v1/triaje/camas/" + p.getCamaId()).withRel("cama"));
                    }
                    return em;
                });
    }

    @GetMapping("/pacientes/{dni}")
    public Mono<ResponseEntity<Paciente>> getPaciente(@PathVariable String dni) {
        return pacienteRepo.findById(dni)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes")
    public Mono<ResponseEntity<Paciente>> addPaciente(@RequestBody Paciente paciente) {
        paciente.setEstado(Estado.ESPERA);
        
        Mono<Cama> occupyNewBed = (paciente.getCamaId() != null) 
            ? camaRepo.findById(paciente.getCamaId()).flatMap(c -> { c.setOcupada(true); return camaRepo.save(c); }) 
            : Mono.empty();

        return occupyNewBed.then(entityTemplate.insert(paciente))
                .flatMap(p -> eventProducer.emitirEventoPaciente(p).thenReturn(p))
                .map(p -> ResponseEntity.status(HttpStatus.CREATED).body(p));
    }

    @PutMapping("/pacientes/{dni}")
    public Mono<ResponseEntity<Paciente>> updatePaciente(@PathVariable String dni, @RequestBody Paciente paciente) {
        return pacienteRepo.findById(dni)
                .flatMap(existing -> {
                    Long oldCamaId = existing.getCamaId();
                    Long newCamaId = paciente.getCamaId();
                    
                    Mono<Cama> freeOldBed = (oldCamaId != null && !oldCamaId.equals(newCamaId)) 
                        ? camaRepo.findById(oldCamaId).flatMap(c -> { c.setOcupada(false); return camaRepo.save(c); }) 
                        : Mono.empty();

                    Mono<Cama> occupyNewBed = (newCamaId != null && !newCamaId.equals(oldCamaId)) 
                        ? camaRepo.findById(newCamaId).flatMap(c -> { c.setOcupada(true); return camaRepo.save(c); }) 
                        : Mono.empty();

                    existing.setNombre(paciente.getNombre());
                    existing.setApellidos(paciente.getApellidos());
                    existing.setGenero(paciente.getGenero());
                    existing.setGrado(paciente.getGrado());
                    existing.setEstado(paciente.getEstado());
                    existing.setCamaId(newCamaId);
                    
                    return freeOldBed.then(occupyNewBed).then(pacienteRepo.save(existing));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/pacientes/{dni}")
    public Mono<ResponseEntity<Void>> deletePaciente(@PathVariable String dni) {
        return pacienteRepo.findById(dni)
                .flatMap(paciente -> {
                    Mono<Cama> freeBed = (paciente.getCamaId() != null)
                            ? camaRepo.findById(paciente.getCamaId()).flatMap(c -> { c.setOcupada(false); return camaRepo.save(c); })
                            : Mono.empty();
                    return freeBed.then(pacienteRepo.deleteById(dni));
                })
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/pacientes/grado/{grado}")
    public Flux<Paciente> getPacientesByGrado(@PathVariable String grado) {
        return pacienteRepo.findByGrado(Grado.valueOf(grado.toUpperCase()));
    }

    @GetMapping("/pacientes/estado/{estado}")
    public Flux<Paciente> getPacientesByEstado(@PathVariable String estado) {
        return pacienteRepo.findByEstado(Estado.valueOf(estado.toUpperCase()));
    }

    @GetMapping("/camas")
    public Flux<org.springframework.hateoas.EntityModel<Cama>> getCamas() {
        return camaRepo.findAll()
                .map(c -> {
                    org.springframework.hateoas.EntityModel<Cama> em = org.springframework.hateoas.EntityModel.of(c);
                    em.add(org.springframework.hateoas.Link.of("/api/v1/triaje/camas/" + c.getId()).withSelfRel());
                    return em;
                });
    }

    @GetMapping("/camas/disponibles")
    public Flux<Cama> getCamasDisponibles() {
        return camaRepo.findByOcupada(false);
    }

    @PostMapping("/camas")
    public Mono<ResponseEntity<Cama>> addCama(@RequestBody Cama cama) {
        return camaRepo.save(cama)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c));
    }

    @PutMapping("/pacientes/{dni}/trasladar/{camaId}")
    public Mono<ResponseEntity<Paciente>> trasladarPaciente(@PathVariable String dni, @PathVariable Long camaId) {
        return pacienteRepo.findById(dni)
                .flatMap(paciente -> {
                    Long oldCamaId = paciente.getCamaId();
                    paciente.setCamaId(camaId);
                    
                    Mono<Cama> freeOldBed = (oldCamaId != null) 
                        ? camaRepo.findById(oldCamaId).flatMap(c -> { c.setOcupada(false); return camaRepo.save(c); }) 
                        : Mono.empty();

                    Mono<Cama> occupyNewBed = camaRepo.findById(camaId).flatMap(c -> { c.setOcupada(true); return camaRepo.save(c); });

                    return freeOldBed.then(occupyNewBed).then(pacienteRepo.save(paciente));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/pacientes/{dni}/procesar")
    public Mono<ResponseEntity<Paciente>> procesarPaciente(@PathVariable String dni) {
        return pacienteRepo.findById(dni)
                .flatMap(p -> {
                    p.setEstado(Estado.ESPERA);
                    return pacienteRepo.save(p)
                            .flatMap(saved -> eventProducer.emitirEventoPaciente(saved).thenReturn(saved));
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/registros")
    public Mono<ResponseEntity<Void>> deleteRegistros(@RequestParam String email) {
        return Mono.just(ResponseEntity.noContent().<Void>build());
    }
}
