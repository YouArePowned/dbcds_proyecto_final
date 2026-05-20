/**
 * Punto de entrada de API3 - Supervisor Médico.
 * Agrega datos de API1 (pacientes/camas) y API2 (lecturas vitales) mediante WebClient.
 * También escucha eventos de RabbitMQ para supervisión del tráfico de triaje.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.supervisor.api3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Api3SupervisorMedicoApplication {
    public static void main(String[] args) {
        SpringApplication.run(Api3SupervisorMedicoApplication.class, args);
    }
}
