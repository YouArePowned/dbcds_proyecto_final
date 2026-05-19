package es.uv.garcosda.sensores.api2.domain;

import java.time.Instant;

public class LecturaVital {
    private String id;
    private String pacienteDni;
    private Instant timestamp;
    private Integer frecuenciaCardiaca;
    private Integer spo2;
    private String presionArterial;
    private Integer etco2;

    public LecturaVital() {}

    public LecturaVital(String id, String pacienteDni, Instant timestamp, Integer frecuenciaCardiaca,
                        Integer spo2, String presionArterial, Integer etco2) {
        this.id = id;
        this.pacienteDni = pacienteDni;
        this.timestamp = timestamp;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.spo2 = spo2;
        this.presionArterial = presionArterial;
        this.etco2 = etco2;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPacienteDni() { return pacienteDni; }
    public void setPacienteDni(String pacienteDni) { this.pacienteDni = pacienteDni; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }
    public Integer getSpo2() { return spo2; }
    public void setSpo2(Integer spo2) { this.spo2 = spo2; }
    public String getPresionArterial() { return presionArterial; }
    public void setPresionArterial(String presionArterial) { this.presionArterial = presionArterial; }
    public Integer getEtco2() { return etco2; }
    public void setEtco2(Integer etco2) { this.etco2 = etco2; }
}
