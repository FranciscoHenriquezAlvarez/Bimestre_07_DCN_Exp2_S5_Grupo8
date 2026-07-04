package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.ArchivoResumenResponseDTO;
import com.duoc.cloudnativeapp.dto.DetalleInscripcionResumenDTO;
import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import com.duoc.cloudnativeapp.exception.ArchivoLocalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ResumenArchivoService {

    private final InscripcionService inscripcionService;
    private final Path resumenesPath;

    public ResumenArchivoService(InscripcionService inscripcionService,
                                 @Value("${app.archivos.resumenes-path:archivos/resumenes}") String resumenesPath) {
        this.inscripcionService = inscripcionService;
        this.resumenesPath = Paths.get(resumenesPath);
    }

    // Genera un archivo de texto con el resumen de la inscripcion.
    public ArchivoResumenResponseDTO generarArchivo(Long inscripcionId) {
        Path archivo = regenerarArchivo(inscripcionId);

        return new ArchivoResumenResponseDTO(
                "Archivo generado correctamente",
                archivo.toAbsolutePath().toString(),
                obtenerNombreArchivo(inscripcionId),
                null,
                null,
                null
        );
    }

    // Si el archivo ya existe localmente, se reutiliza para subirlo a S3.
    public Path obtenerArchivoLocalOGenerar(Long inscripcionId) {
        inscripcionService.obtenerPorId(inscripcionId);

        Path archivo = obtenerRutaArchivo(inscripcionId);
        if (Files.exists(archivo)) {
            return archivo;
        }

        return regenerarArchivo(inscripcionId);
    }

    // Regenera el archivo usando la informacion actual de la inscripcion.
    public Path regenerarArchivo(Long inscripcionId) {
        InscripcionResumenDTO resumen = inscripcionService.obtenerPorId(inscripcionId);

        try {
            Files.createDirectories(resumenesPath);
            Path archivo = obtenerRutaArchivo(inscripcionId);
            Files.writeString(archivo, construirContenido(resumen), StandardCharsets.UTF_8);
            return archivo;
        } catch (IOException | SecurityException exception) {
            throw new ArchivoLocalException("No fue posible generar el archivo local del resumen", exception);
        }
    }

    public Path obtenerRutaArchivo(Long inscripcionId) {
        return resumenesPath.resolve(obtenerNombreArchivo(inscripcionId));
    }

    public String obtenerNombreArchivo(Long inscripcionId) {
        return "resumen-inscripcion-" + inscripcionId + ".txt";
    }

    // Construye el contenido que quedara guardado en el archivo .txt.
    private String construirContenido(InscripcionResumenDTO resumen) {
        String salto = System.lineSeparator();
        StringBuilder contenido = new StringBuilder();

        contenido.append("Numero de inscripcion: ")
                .append(resumen.getInscripcionId())
                .append(salto);
        contenido.append("Nombre del estudiante: ")
                .append(resumen.getEstudiante())
                .append(salto);
        contenido.append("Fecha de inscripcion: ")
                .append(resumen.getFechaInscripcion())
                .append(salto);
        contenido.append("Cursos inscritos:")
                .append(salto);

        for (DetalleInscripcionResumenDTO curso : resumen.getCursos()) {
            contenido.append("- ")
                    .append(curso.getNombre())
                    .append(" | Costo: ")
                    .append(curso.getCosto())
                    .append(salto);
        }

        contenido.append("Total a pagar: ")
                .append(resumen.getTotalPagar())
                .append(salto);

        return contenido.toString();
    }
}
