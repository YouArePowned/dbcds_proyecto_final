package es.uv.garcosda.domain;

import java.io.Serializable;

/**
 * Clase que representa un paciente registrado en el triaje de urgencias.
 * Contiene los datos personales del paciente, su grado de gravedad y su estado actual.
 * Implementa Serializable para permitir la serialización JSON a través de RabbitMQ.
 */
public class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dni;
    private String nombre;
    private String apellidos;
    private String genero;
    private Grado grado;
    private Estado estado;

    public Paciente() {}

    public Paciente(String dni, String nombre, String apellidos, String genero, Grado grado, Estado estado) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.genero = genero;
        this.grado = grado;
        this.estado = estado;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("Paciente{dni='%s', nombre='%s', apellidos='%s', genero='%s', grado=%s, estado=%s}",
                dni, nombre, apellidos, genero, grado, estado);
    }
}
