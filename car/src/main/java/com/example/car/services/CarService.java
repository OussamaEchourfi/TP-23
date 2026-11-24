package com.example.car.services;

import com.example.car.clients.ClientRemote;
import com.example.car.entities.Car;
import com.example.car.entities.Client;
import com.example.car.entities.ClientPlaceholder;
import com.example.car.models.CarResponse;
import com.example.car.repositories.CarRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final ClientRemote clientRemote;

    public CarService(CarRepository carRepository, ClientRemote clientRemote) {
        this.carRepository = carRepository;
        this.clientRemote = clientRemote;
    }

    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();
        Client[] clients = clientRemote.tous();
        return cars.stream().map(car -> map(car, chercherClient(clients, car))).toList();
    }

    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        return map(car, clientRemote.parId(car.getClient_id()));
    }

    private Client chercherClient(Client[] clients, Car car) {
        if (clients == null) {
            return ClientPlaceholder.of(car.getClient_id());
        }
        return Arrays.stream(clients)
                .filter(client -> client.getId() != null && client.getId().equals(car.getClient_id()))
                .findFirst()
                .orElse(ClientPlaceholder.of(car.getClient_id()));
    }

    private CarResponse map(Car car, Client client) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .matricue(car.getMatricule())
                .client(client)
                .build();
    }
}
