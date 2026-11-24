package com.example.car;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.car.entities.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.car.clients.ClientRemote;

@SpringBootTest(properties = {
        "spring.cloud.consul.enabled=false",
        "spring.cloud.consul.discovery.enabled=false",
        "spring.cloud.consul.config.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
@AutoConfigureMockMvc
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRemote clientRemote;

    @Test
    void jointLeClientSurChaqueVoiture() throws Exception {
        Client[] clients = {new Client(1L, "Amine SAFI", 23), new Client(2L, "Amal ALAOUI", 22)};
        given(clientRemote.tous()).willReturn(clients);

        mockMvc.perform(get("/api/car"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].client.nom").value("Amine SAFI"));
    }

    @Test
    void trouveUneVoitureAvecSonClient() throws Exception {
        given(clientRemote.parId(anyLong())).willReturn(new Client(1L, "Amine SAFI", 23));

        mockMvc.perform(get("/api/car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.client.nom").value("Amine SAFI"));
    }
}
