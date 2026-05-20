/**
 * Configuración de SpringDoc / OpenAPI para documentar los endpoints de API1.
 *
 * Autores: Victor Sanz, Carlos Marques, Sara Cardenas
 */
package es.uv.garcosda.triaje.api1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API 1 - Triaje y Camas", version = "v1"))
public class SpringDocConfig {
}
