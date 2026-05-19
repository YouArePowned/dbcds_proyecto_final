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
