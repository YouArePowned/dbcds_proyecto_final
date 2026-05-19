package es.uv.garcosda.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.uv.garcosda.domain.Estado;
import es.uv.garcosda.domain.Grado;
import es.uv.garcosda.domain.Paciente;

/**
 * Servicio que gestiona la lista de pacientes en memoria.
 * Proporciona operaciones CRUD completas y consultas por DNI, grado y estado.
 */
@Service
public class PacienteService {

    private List<Paciente> pacientes;

    public PacienteService() {
        pacientes = new ArrayList<Paciente>(
            Arrays.asList(
                new Paciente("12345678A", "Juan", "García López", "M", Grado.LEVE, Estado.ESPERA),
                new Paciente("23456789B", "María", "Fernández Ruiz", "F", Grado.GRAVE, Estado.ESPERA),
                new Paciente("34567890C", "Pedro", "Martínez Sánchez", "M", Grado.CRITICO, Estado.ESPERA),
                new Paciente("45678901D", "Ana", "López Díaz", "F", Grado.LEVE, Estado.CONSULTA),
                new Paciente("56789012E", "Carlos", "Rodríguez Pérez", "M", Grado.GRAVE, Estado.ALTA)
            )
        );
    }

    /**
     * Devuelve la lista completa de pacientes registrados.
     * @return lista de todos los pacientes
     */
    public List<Paciente> findAll() {
        return this.pacientes;
    }

    /**
     * Busca un paciente por su DNI.
     * @param dni el DNI del paciente a buscar
     * @return el paciente encontrado o null si no existe
     */
    public Paciente findByDni(String dni) {
        List<Paciente> ps = this.pacientes.stream()
                .filter(x -> x.getDni().equalsIgnoreCase(dni))
                .collect(Collectors.toList());
        if (ps.size() > 0) {
            return ps.get(0);
        }
        return null;
    }

    /**
     * Busca todos los pacientes que tienen un determinado grado de gravedad.
     * @param grado el grado a filtrar (LEVE, GRAVE, CRITICO)
     * @return lista de pacientes con ese grado
     */
    public List<Paciente> findByGrado(Grado grado) {
        return this.pacientes.stream()
                .filter(x -> x.getGrado() == grado)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos los pacientes que tienen un determinado estado.
     * @param estado el estado a filtrar (ESPERA, CONSULTA, ALTA)
     * @return lista de pacientes con ese estado
     */
    public List<Paciente> findByEstado(Estado estado) {
        return this.pacientes.stream()
                .filter(x -> x.getEstado() == estado)
                .collect(Collectors.toList());
    }

    /**
     * Añade un nuevo paciente a la lista.
     * Verifica que no exista previamente un paciente con el mismo DNI.
     * @param paciente el paciente a registrar
     * @return true si se añadió correctamente, false si ya existía un paciente con ese DNI
     */
    public boolean addPaciente(Paciente paciente) {
        // Verificar que no exista ya un paciente con el mismo DNI
        if (findByDni(paciente.getDni()) != null) {
            return false;
        }
        return this.pacientes.add(paciente);
    }

    /**
     * Actualiza los datos de un paciente existente identificado por su DNI.
     * @param dni el DNI del paciente a actualizar
     * @param paciente los nuevos datos del paciente
     * @return el paciente actualizado o null si no se encontró
     */
    public Paciente updatePaciente(String dni, Paciente paciente) {
        Paciente existing = findByDni(dni);
        if (existing == null) {
            return null;
        }
        existing.setNombre(paciente.getNombre());
        existing.setApellidos(paciente.getApellidos());
        existing.setGenero(paciente.getGenero());
        existing.setGrado(paciente.getGrado());
        existing.setEstado(paciente.getEstado());
        return existing;
    }

    /**
     * Elimina un paciente de la lista identificado por su DNI.
     * @param dni el DNI del paciente a eliminar
     * @return true si se eliminó correctamente, false si no se encontró
     */
    public boolean deletePaciente(String dni) {
        Paciente p = findByDni(dni);
        if (p == null) {
            return false;
        }
        return this.pacientes.remove(p);
    }
}
