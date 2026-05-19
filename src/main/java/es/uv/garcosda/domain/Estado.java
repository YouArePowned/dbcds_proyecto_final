package es.uv.garcosda.domain;

/**
 * Enum que representa el estado actual del paciente en el proceso de atención.
 * ESPERA: el paciente ha sido registrado y espera a ser atendido.
 * CONSULTA: el paciente está siendo atendido por un médico.
 * ALTA: el paciente ha sido dado de alta.
 */
public enum Estado {
    ESPERA,
    CONSULTA,
    ALTA
}
