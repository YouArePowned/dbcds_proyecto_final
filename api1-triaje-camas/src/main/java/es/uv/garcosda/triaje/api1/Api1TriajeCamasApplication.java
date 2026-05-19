package es.uv.garcosda.triaje.api1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Api1TriajeCamasApplication {
    public static void main(String[] args) {
        SpringApplication.run(Api1TriajeCamasApplication.class, args);
    }
}
