package es.uv.garcosda.domain;

/**
 * Enum que representa el grado de gravedad del paciente en triaje.
 * Se utiliza como routing key para dirigir los mensajes a la cola correspondiente.
 */
public enum Grado {
    LEVE,
    GRAVE,
    CRITICO
}
