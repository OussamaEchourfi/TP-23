package com.example.car.clients;

import com.example.car.entities.Client;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Appels distants vers SERVICE-CLIENT, resolus par Consul (nom logique + LoadBalancer).
 * Les fallbacks sont volontairement autonomes (aucune dependance injectee) afin de
 * rester fiables meme quand le circuit est ouvert.
 */
@Component
public class ClientRemote {

    private static final String URL = "http://SERVICE-CLIENT";

    private final RestTemplate restTemplate;

    public ClientRemote(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "clientService", fallbackMethod = "tousIndisponibles")
    public Client[] tous() {
        return restTemplate.getForObject(URL + "/api/client", Client[].class);
    }

    @CircuitBreaker(name = "clientService", fallbackMethod = "unIndisponible")
    public Client parId(Long id) {
        return restTemplate.getForObject(URL + "/api/client/" + id, Client.class);
    }

    @SuppressWarnings("unused")
    private Client[] tousIndisponibles(Throwable cause) {
        return new Client[0];
    }

    @SuppressWarnings("unused")
    private Client unIndisponible(Long id, Throwable cause) {
        return Client.builder().id(id).nom("SERVICE-CLIENT INDISPONIBLE").age(0).build();
    }
}
