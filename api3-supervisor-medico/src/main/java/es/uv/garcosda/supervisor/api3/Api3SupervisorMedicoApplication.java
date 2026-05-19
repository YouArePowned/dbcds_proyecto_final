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
