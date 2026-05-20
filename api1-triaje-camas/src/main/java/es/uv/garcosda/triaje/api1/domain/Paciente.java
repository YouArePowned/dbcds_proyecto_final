/**
 * Entidad reactiva que representa un paciente en el sistema de triaje.
 * Mapeada a la tabla "pacientes" en H2 vía R2DBC.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("pacientes")
public class Paciente {
    @Id
    private String dni;
    private String nombre;
    private String apellidos;
    private String genero;
    private Grado grado;
    private Estado estado;
    private Long camaId;

    public Paciente() {}

    public Paciente(String dni, String nombre, String apellidos, String genero, Grado grado, Estado estado, Long camaId) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.genero = genero;
        this.grado = grado;
        this.estado = estado;
        this.camaId = camaId;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Grado getGrado() { return grado; }
    public void setGrado(Grado grado) { this.grado = grado; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Long getCamaId() { return camaId; }
    public void setCamaId(Long camaId) { this.camaId = camaId; }
}
