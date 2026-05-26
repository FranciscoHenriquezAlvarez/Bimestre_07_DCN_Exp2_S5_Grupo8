package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.model.Curso;
import com.duoc.cloudnativeapp.repository.CursoRepository;
import com.duoc.cloudnativeapp.repository.DetalleInscripcionRepository;
import com.duoc.cloudnativeapp.repository.EstudianteRepository;
import com.duoc.cloudnativeapp.repository.InscripcionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CursoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DetalleInscripcionRepository detalleInscripcionRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @BeforeEach
    void setUp() {
        // Limpia primero las tablas hijas para evitar errores de llave foranea
        detalleInscripcionRepository.deleteAll();

        // Luego limpia las tablas principales relacionadas
        inscripcionRepository.deleteAll();
        estudianteRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    @Test
    void debeCrearCurso() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new CursoPayload(
                "Spring Boot Basico",
                "Carlos Valverde",
                20,
                new BigDecimal("50000")
        ));

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Spring Boot Basico"))
                .andExpect(jsonPath("$.instructor").value("Carlos Valverde"))
                .andExpect(jsonPath("$.duracionHoras").value(20));
    }

    @Test
    void debeListarCursosDisponibles() throws Exception {
        cursoRepository.save(crearCurso("Spring Boot Basico", "Carlos Valverde", 20, "50000"));
        cursoRepository.save(crearCurso("Docker Basico", "Ana Soto", 16, "40000"));

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Spring Boot Basico"))
                .andExpect(jsonPath("$[1].nombre").value("Docker Basico"));
    }

    private Curso crearCurso(String nombre, String instructor, Integer duracionHoras, String costo) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setInstructor(instructor);
        curso.setDuracionHoras(duracionHoras);
        curso.setCosto(new BigDecimal(costo));
        return curso;
    }

    private record CursoPayload(String nombre, String instructor, Integer duracionHoras, BigDecimal costo) {
    }
}
