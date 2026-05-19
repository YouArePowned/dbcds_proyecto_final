package es.uv.garcosda.triaje.api1.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("camas")
public class Cama {
    @Id
    private Long id;
    private String codigo;
    private Long plantaId;
    private Boolean ocupada;
    private Double latitud;
    private Double longitud;

    public Cama() {}

    public Cama(Long id, String codigo, Long plantaId, Boolean ocupada, Double latitud, Double longitud) {
        this.id = id;
        this.codigo = codigo;
        this.plantaId = plantaId;
        this.ocupada = ocupada;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public Long getPlantaId() { return plantaId; }
    public void setPlantaId(Long plantaId) { this.plantaId = plantaId; }
    public Boolean getOcupada() { return ocupada; }
    public void setOcupada(Boolean ocupada) { this.ocupada = ocupada; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
