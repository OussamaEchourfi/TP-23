package com.example.client;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.discovery.enabled=false",
        "spring.cloud.consul.config.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listeLesClientsInitialises() throws Exception {
        mockMvc.perform(get("/api/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Amine SAFI"));
    }

    @Test
    void trouveUnClientParId() throws Exception {
        mockMvc.perform(get("/api/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Amine SAFI"));
    }

    @Test
    void renvoieUneErreurSurIdInconnu() throws Exception {
        mockMvc.perform(get("/api/client/9999"))
                .andExpect(status().isNotFound());
    }
}
