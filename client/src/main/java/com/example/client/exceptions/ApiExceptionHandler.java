package com.example.client.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(ClientNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle("Client introuvable");
        detail.setProperty("service", "SERVICE-CLIENT");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    /** Compatibilite : le service historique levait un java.lang.Exception brut. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> generic(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("service", "SERVICE-CLIENT", "message", String.valueOf(ex.getMessage())));
    }
}
