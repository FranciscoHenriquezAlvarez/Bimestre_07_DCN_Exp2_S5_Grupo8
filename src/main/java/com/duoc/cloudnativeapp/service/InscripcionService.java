package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.DetalleInscripcionResumenDTO;
import com.duoc.cloudnativeapp.dto.InscripcionRequestDTO;
import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import com.duoc.cloudnativeapp.model.Curso;
import com.duoc.cloudnativeapp.model.DetalleInscripcion;
import com.duoc.cloudnativeapp.model.Estudiante;
import com.duoc.cloudnativeapp.model.Inscripcion;
import com.duoc.cloudnativeapp.repository.CursoRepository;
import com.duoc.cloudnativeapp.repository.EstudianteRepository;
import com.duoc.cloudnativeapp.repository.InscripcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Servicio que registra inscripciones y calcula su total
@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;

    // Inyeccion de repositorios por constructor
    public InscripcionService(InscripcionRepository inscripcionRepository,
                              EstudianteRepository estudianteRepository,
                              CursoRepository cursoRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<InscripcionResumenDTO> obtenerTodas() {
        return inscripcionRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResumenDTO)
                .toList();
    }

    public InscripcionResumenDTO guardar(InscripcionRequestDTO inscripcionRequestDTO) {
        // Valida que el estudiante exista antes de registrar la inscripcion
        Estudiante estudiante = estudianteRepository.findById(inscripcionRequestDTO.getEstudianteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        if (inscripcionRequestDTO.getCursosIds() == null || inscripcionRequestDTO.getCursosIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe enviar al menos un curso");
        }

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setFechaInscripcion(LocalDate.now());

        List<DetalleInscripcion> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Long cursoId : inscripcionRequestDTO.getCursosIds()) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado: " + cursoId));

            DetalleInscripcion detalle = new DetalleInscripcion();
            detalle.setInscripcion(inscripcion);
            detalle.setCurso(curso);
            detalle.setCostoCurso(curso.getCosto());
            detalles.add(detalle);

            // Calcula el total sumando el costo de los cursos seleccionados
            total = total.add(curso.getCosto());
        }

        inscripcion.setDetalles(detalles);
        inscripcion.setTotal(total);

        return convertirAResumenDTO(inscripcionRepository.save(inscripcion));
    }

    // Convierte la inscripcion guardada en un resumen facil de leer
    private InscripcionResumenDTO convertirAResumenDTO(Inscripcion inscripcion) {
        List<DetalleInscripcionResumenDTO> cursos = inscripcion.getDetalles()
                .stream()
                .map(detalle -> new DetalleInscripcionResumenDTO(
                        detalle.getCurso().getId(),
                        detalle.getCurso().getNombre(),
                        detalle.getCostoCurso()
                ))
                .toList();

        return new InscripcionResumenDTO(
                inscripcion.getId(),
                inscripcion.getEstudiante().getNombre(),
                inscripcion.getFechaInscripcion(),
                cursos,
                inscripcion.getTotal()
        );
    }
}
