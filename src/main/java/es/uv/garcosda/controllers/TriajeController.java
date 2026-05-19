package es.uv.garcosda.controllers;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.uv.garcosda.domain.Estado;
import es.uv.garcosda.domain.Grado;
import es.uv.garcosda.domain.Paciente;
import es.uv.garcosda.services.PacienteService;

/**
 * Controlador REST del módulo Triaje (Productor).
 * Expone la API REST para el registro y gestión de pacientes en urgencias.
 * Al registrar un nuevo paciente, envía un mensaje a RabbitMQ con la routing key
 * para que sea procesado por los consumidores de atención y el supervisor.
 */
@RestController
@RequestMapping("/api/v1")
public class TriajeController {

    @Autowired
    PacienteService ps;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    /**
     * Obtiene la lista completa de pacientes registrados en el sistema.
     * @return lista de pacientes con código 200 OK
     */
    @GetMapping("pacientes")
    public ResponseEntity<?> getPacientes() {
        List<Paciente> pacientes = this.ps.findAll();
        return new ResponseEntity<List<Paciente>>(pacientes, HttpStatus.OK);
    }

    /**
     * Busca un paciente por su DNI.
     * @param dni el Documento Nacional de Identidad del paciente
     * @return el paciente encontrado (200 OK) o mensaje de error (404 NOT FOUND)
     */
    @GetMapping("pacientes/{dni}")
    public ResponseEntity<?> getPaciente(@PathVariable("dni") String dni) {
        Paciente p = this.ps.findByDni(dni);
        if (p == null) {
            return new ResponseEntity<String>("No se encontró paciente con DNI " + dni, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Paciente>(p, HttpStatus.OK);
    }

    /**
     * Busca pacientes por su grado de gravedad.
     * @param grado el grado a filtrar (LEVE, GRAVE, CRITICO)
     * @return lista de pacientes con ese grado (200 OK)
     */
    @GetMapping("pacientes/grado/{grado}")
    public ResponseEntity<?> getPacientesByGrado(@PathVariable("grado") String grado) {
        try {
            Grado g = Grado.valueOf(grado.toUpperCase());
            List<Paciente> pacientes = this.ps.findByGrado(g);
            return new ResponseEntity<List<Paciente>>(pacientes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>("Grado no válido: " + grado + ". Valores permitidos: LEVE, GRAVE, CRITICO", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Busca pacientes por su estado actual.
     * @param estado el estado a filtrar (ESPERA, CONSULTA, ALTA)
     * @return lista de pacientes con ese estado (200 OK)
     */
    @GetMapping("pacientes/estado/{estado}")
    public ResponseEntity<?> getPacientesByEstado(@PathVariable("estado") String estado) {
        try {
            Estado e = Estado.valueOf(estado.toUpperCase());
            List<Paciente> pacientes = this.ps.findByEstado(e);
            return new ResponseEntity<List<Paciente>>(pacientes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>("Estado no válido: " + estado + ". Valores permitidos: ESPERA, CONSULTA, ALTA", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Registra un nuevo paciente en el sistema de triaje.
     * @param paciente los datos del paciente a registrar
     * @return mensaje de confirmación (201 CREATED) o error (409 CONFLICT)
     */
    @PostMapping("pacientes")
    public ResponseEntity<?> addPaciente(@RequestBody Paciente paciente) {
        // El paciente entra siempre en estado ESPERA al registrarse en triaje
        paciente.setEstado(Estado.ESPERA);

        if (this.ps.addPaciente(paciente)) {
            // Construir la routing key según el grado del paciente: "pacientes.leve", "pacientes.grave" o "pacientes.critico"
            String routingKey = "pacientes." + paciente.getGrado().name().toLowerCase();

            // Enviar mensaje al exchange de tipo Topic para que los consumidores lo procesen
            this.rabbitTemplate.convertAndSend(this.exchange, routingKey, paciente);

            return new ResponseEntity<String>("Paciente registrado en triaje con DNI " + paciente.getDni(), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<String>("Ya existe un paciente con DNI " + paciente.getDni(), HttpStatus.CONFLICT);
        }
    }

    /**
     * Actualiza los datos de un paciente existente.
     * @param dni el DNI del paciente a actualizar
     * @param paciente los nuevos datos del paciente
     * @return el paciente actualizado (200 OK) o error (404 NOT FOUND)
     */
    @PutMapping("pacientes/{dni}")
    public ResponseEntity<?> updatePaciente(@PathVariable("dni") String dni, @RequestBody Paciente paciente) {
        Paciente updated = this.ps.updatePaciente(dni, paciente);
        if (updated == null) {
            return new ResponseEntity<String>("No se encontró paciente con DNI " + dni, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Paciente>(updated, HttpStatus.OK);
    }

    /**
     * Elimina un paciente del sistema.
     * @param dni el DNI del paciente a eliminar
     * @return mensaje de confirmación (200 OK) o error (404 NOT FOUND)
     */
    @DeleteMapping("pacientes/{dni}")
    public ResponseEntity<?> deletePaciente(@PathVariable("dni") String dni) {
        if (this.ps.deletePaciente(dni)) {
            return new ResponseEntity<String>("Paciente con DNI " + dni + " eliminado", HttpStatus.OK);
        }
        return new ResponseEntity<String>("No se encontró paciente con DNI " + dni, HttpStatus.NOT_FOUND);
    }
}
