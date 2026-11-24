package com.fateway.gateway.api;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * Exposition de l'etat reel du registre Consul, consommee par la console de migration.
 * Permet de prouver en direct que Consul (et non Eureka) est la source de verite.
 */
@RestController
@RequestMapping("/api/migration")
public class ConsulMigrationController {

    private final DiscoveryClient discoveryClient;

    public ConsulMigrationController(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/registry")
    public Mono<Map<String, Object>> registry() {
        Set<String> services = new TreeSet<>(discoveryClient.getServices());
        List<Map<String, Object>> instances = new ArrayList<>();

        for (String id : services) {
            for (ServiceInstance instance : discoveryClient.getInstances(id)) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("service", id);
                row.put("instanceId", instance.getInstanceId());
                row.put("host", instance.getHost());
                row.put("port", instance.getPort());
                row.put("uri", instance.getUri().toString());
                row.put("secure", instance.isSecure());
                row.put("tags", instance.getMetadata());
                instances.add(row);
            }
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("provider", "CONSUL");
        body.put("consulUi", "http://localhost:8500/ui/dc1/services");
        body.put("generatedAt", OffsetDateTime.now().toString());
        body.put("serviceCount", services.size());
        body.put("instanceCount", instances.size());
        body.put("services", services);
        body.put("instances", instances);
        return Mono.just(body);
    }

    @GetMapping("/instances/{serviceId}")
    public Mono<List<Map<String, Object>>> instances(@PathVariable String serviceId) {
        List<Map<String, Object>> rows = discoveryClient.getInstances(serviceId).stream()
                .map(instance -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("instanceId", instance.getInstanceId());
                    row.put("uri", instance.getUri().toString());
                    row.put("metadata", instance.getMetadata());
                    return row;
                })
                .toList();
        return Mono.just(rows);
    }

    @GetMapping("/health")
    public Mono<Map<String, Object>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        Set<String> services = new TreeSet<>(discoveryClient.getServices());
        body.put("consulReachable", !services.isEmpty());
        body.put("eurekaExpected", false);
        body.put("services", services);
        body.put("eurekaResidual", services.stream()
                .filter(s -> s.toUpperCase().contains("EUREKA"))
                .toList());
        return Mono.just(body);
    }
}
