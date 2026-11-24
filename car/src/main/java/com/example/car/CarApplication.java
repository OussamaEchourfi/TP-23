package com.example.car;

import com.example.car.entities.Car;
import com.example.car.repositories.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class CarApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarApplication.class, args);
    }

    /**
     * Compatibilite Eureka -> Consul : l'URL passe de http://localhost:8888/SERVICE-CLIENT
     * (Gateway + Eureka) a http://SERVICE-CLIENT (nom logique resolut par Consul).
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    CommandLineRunner initialiserBase(CarRepository carRepository) {
        return args -> {
            if (carRepository.count() > 0) {
                return;
            }
            carRepository.save(new Car(null, "Toyota", "Corolla", "A 25 333", 1L));
            carRepository.save(new Car(null, "Renault", "Megane", "B 6 3456", 2L));
            carRepository.save(new Car(null, "Peugeot", "301", "A 55 4444", 1L));
        };
    }
}
