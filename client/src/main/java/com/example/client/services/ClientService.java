package com.example.client.services;

import com.example.client.entities.Client;
import com.example.client.exceptions.ClientNotFoundException;
import com.example.client.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client addClient(Client client) {
        client.setId(null);
        return clientRepository.save(client);
    }
}
