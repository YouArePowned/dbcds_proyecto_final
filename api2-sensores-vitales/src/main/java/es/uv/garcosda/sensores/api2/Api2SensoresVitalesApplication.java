/**
 * Punto de entrada de API2 - Sensores Vitales.
 * Recibe y almacena lecturas de constantes vitales en memoria (ConcurrentHashMap).
 * No utiliza MongoDB real; los comentarios en el UI hacen referencia a ello por diseño académico.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.sensores.api2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Api2SensoresVitalesApplication {
    public static void main(String[] args) {
        SpringApplication.run(Api2SensoresVitalesApplication.class, args);
    }
}
