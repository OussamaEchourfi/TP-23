package com.example.client.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Client introuvable : " + id);
    }
}
