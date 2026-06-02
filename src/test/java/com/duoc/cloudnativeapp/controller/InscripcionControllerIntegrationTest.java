package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.model.Curso;
import com.duoc.cloudnativeapp.model.Estudiante;
import com.duoc.cloudnativeapp.repository.CursoRepository;
import com.duoc.cloudnativeapp.repository.DetalleInscripcionRepository;
import com.duoc.cloudnativeapp.repository.EstudianteRepository;
import com.duoc.cloudnativeapp.repository.InscripcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InscripcionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private DetalleInscripcionRepository detalleInscripcionRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Value("${app.archivos.resumenes-path}")
    private String resumenesPath;

    @BeforeEach
    void setUp() throws IOException {
        // Limpia primero las tablas hijas para evitar errores de llave foranea
        detalleInscripcionRepository.deleteAll();

        // Luego limpia las tablas principales relacionadas
        inscripcionRepository.deleteAll();
        estudianteRepository.deleteAll();
        cursoRepository.deleteAll();

        limpiarResumenes();
    }

    @Test
    void debeCrearInscripcionConResumen() throws Exception {
        Estudiante estudiante = crearEstudiante("Francisco Henriquez", "francisco@email.com");
        Curso cursoUno = cursoRepository.save(crearCurso("Spring Boot Basico", "Carlos Valverde", 20, "50000"));
        Curso cursoDos = cursoRepository.save(crearCurso("Docker Basico", "Ana Soto", 16, "40000"));

        String requestBody = objectMapper.writeValueAsString(new InscripcionPayload(
                estudiante.getId(),
                List.of(cursoUno.getId(), cursoDos.getId())
        ));

        mockMvc.perform(post("/api/inscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.inscripcionId").isNumber())
                .andExpect(jsonPath("$.estudiante").value("Francisco Henriquez"))
                .andExpect(jsonPath("$.cursos[0].nombre").value("Spring Boot Basico"))
                .andExpect(jsonPath("$.cursos[1].nombre").value("Docker Basico"));
    }

    @Test
    void debeCalcularElTotalDeLaInscripcion() throws Exception {
        Estudiante estudiante = crearEstudiante("Maria Lopez", "maria@email.com");
        Curso cursoUno = cursoRepository.save(crearCurso("Spring Boot Basico", "Carlos Valverde", 20, "50000"));
        Curso cursoDos = cursoRepository.save(crearCurso("Docker Basico", "Ana Soto", 16, "40000"));

        String requestBody = objectMapper.writeValueAsString(new InscripcionPayload(
                estudiante.getId(),
                List.of(cursoUno.getId(), cursoDos.getId())
        ));

        MvcResult result = mockMvc.perform(post("/api/inscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(response.get("totalPagar").decimalValue()).isEqualByComparingTo(new BigDecimal("90000"));
    }

    @Test
    void debeRetornar404CuandoLaInscripcionNoExisteAlGenerarArchivo() throws Exception {
        mockMvc.perform(post("/api/inscripciones/999/generar-archivo"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inscripcion no encontrada"));
    }

    private Estudiante crearEstudiante(String nombre, String correo) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setCorreo(correo);
        return estudianteRepository.save(estudiante);
    }

    private Curso crearCurso(String nombre, String instructor, Integer duracionHoras, String costo) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setInstructor(instructor);
        curso.setDuracionHoras(duracionHoras);
        curso.setCosto(new BigDecimal(costo));
        return curso;
    }

    private void limpiarResumenes() throws IOException {
        Path directorio = Path.of(resumenesPath);
        if (!Files.exists(directorio)) {
            return;
        }

        try (var paths = Files.walk(directorio)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
        }
    }

    private record InscripcionPayload(Long estudianteId, List<Long> cursosIds) {
    }
}
