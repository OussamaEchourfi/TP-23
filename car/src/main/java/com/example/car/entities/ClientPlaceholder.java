package com.example.car.entities;

/** Client de substitution quand SERVICE-CLIENT est injoignable (circuit ouvert). */
public final class ClientPlaceholder {

    private ClientPlaceholder() {
    }

    public static Client of(Long id) {
        return Client.builder()
                .id(id)
                .nom("SERVICE-CLIENT INDISPONIBLE")
                .age(0)
                .build();
    }
}
