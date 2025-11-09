package org.example.simulacionpedidos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SimulationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void simularEndpointRespondsOkAndRendersView() throws Exception {
        mvc.perform(get("/simular"))
           .andExpect(status().isOk())
           .andExpect(view().name("simulation"))
           // el modelo puede variar; verificamos presencia de atributos clave si existen
           .andExpect(model().attributeExists("logs"))
           .andExpect(model().attributeExists("hasRun"))
           .andExpect(model().attributeExists("totalTime"));
    }
}