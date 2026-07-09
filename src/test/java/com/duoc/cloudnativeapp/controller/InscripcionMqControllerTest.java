package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.dto.ResumenInscripcionMensaje;
import com.duoc.cloudnativeapp.service.ResumenInscripcionMqService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class InscripcionMqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResumenInscripcionMqService resumenInscripcionMqService;

    @Test
    void debeInvocarElServicioAlEnviarResumenAMq() throws Exception {
        when(resumenInscripcionMqService.enviarResumenACola(1L))
                .thenReturn(new ResumenInscripcionMensaje(
                        1L,
                        "Francisco Henriquez",
                        "Spring Boot Basico",
                        "2026-07-08",
                        50000.0,
                        "Resumen simple"
                ));

        mockMvc.perform(post("/api/inscripciones/1/enviar-mq"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inscripcionId").value(1))
                .andExpect(jsonPath("$.estudiante").value("Francisco Henriquez"));

        verify(resumenInscripcionMqService).enviarResumenACola(1L);
    }
}
