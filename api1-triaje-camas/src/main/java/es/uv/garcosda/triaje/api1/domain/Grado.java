/**
 * Enumeración del grado de triaje (severidad) de un paciente.
 * Determina la cola de RabbitMQ a la que se enruta el evento.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.domain;

public enum Grado {
    LEVE,
    GRAVE,
    CRITICO
}
