package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.ArchivoResumenResponseDTO;
import com.duoc.cloudnativeapp.dto.DetalleInscripcionResumenDTO;
import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumenArchivoServiceTest {

    @Mock
    private InscripcionService inscripcionService;

    @TempDir
    Path tempDir;

    @Test
    void debeGenerarArchivoLocalDeResumen() throws Exception {
        when(inscripcionService.obtenerPorId(1L)).thenReturn(new InscripcionResumenDTO(
                1L,
                "Francisco Henriquez",
                LocalDate.of(2026, 6, 1),
                List.of(
                        new DetalleInscripcionResumenDTO(10L, "Spring Boot Basico", new BigDecimal("50000")),
                        new DetalleInscripcionResumenDTO(20L, "Docker Basico", new BigDecimal("40000"))
                ),
                new BigDecimal("90000")
        ));

        ResumenArchivoService resumenArchivoService =
                new ResumenArchivoService(inscripcionService, tempDir.toString());

        ArchivoResumenResponseDTO respuesta = resumenArchivoService.generarArchivo(1L);

        Path archivoGenerado = tempDir.resolve("resumen-inscripcion-1.txt");
        assertThat(Files.exists(archivoGenerado)).isTrue();
        assertThat(respuesta.getNombreArchivo()).isEqualTo("resumen-inscripcion-1.txt");
        assertThat(respuesta.getRutaLocal()).endsWith("resumen-inscripcion-1.txt");
        assertThat(Files.readString(archivoGenerado))
                .contains("Numero de inscripcion: 1")
                .contains("Nombre del estudiante: Francisco Henriquez")
                .contains("Spring Boot Basico")
                .contains("Docker Basico")
                .contains("Total a pagar: 90000");
    }
}
