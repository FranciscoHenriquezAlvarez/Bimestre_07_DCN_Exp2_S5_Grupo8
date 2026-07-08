package com.duoc.cloudnativeapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debePermitirApiHealthSinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    void debeResponder401EnCursosSinToken() throws Exception {
        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debePermitirCursosConAuthorityProfesor() throws Exception {
        mockMvc.perform(get("/api/cursos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("PROFESOR"))))
                .andExpect(status().isOk());
    }

    @Test
    void debeResponder403EnCursosConAuthorityEstudiante() throws Exception {
        mockMvc.perform(get("/api/cursos")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ESTUDIANTE"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeResponder401EnDescargaS3SinToken() throws Exception {
        mockMvc.perform(get("/api/inscripciones/1/descargar-s3"))
                .andExpect(status().isUnauthorized());
    }
}
