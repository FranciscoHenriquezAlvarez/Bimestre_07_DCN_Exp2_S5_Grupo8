package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.EstudianteRequestDTO;
import com.duoc.cloudnativeapp.dto.EstudianteResponseDTO;
import com.duoc.cloudnativeapp.model.Estudiante;
import com.duoc.cloudnativeapp.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio con la logica simple de estudiantes
@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    // Inyeccion del repositorio por constructor
    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    public List<EstudianteResponseDTO> obtenerTodos() {
        return estudianteRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public EstudianteResponseDTO guardar(EstudianteRequestDTO estudianteRequestDTO) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(estudianteRequestDTO.getNombre());
        estudiante.setCorreo(estudianteRequestDTO.getCorreo());

        return convertirAResponseDTO(estudianteRepository.save(estudiante));
    }

    // Convierte la entidad a DTO para no exponer datos internos
    private EstudianteResponseDTO convertirAResponseDTO(Estudiante estudiante) {
        return new EstudianteResponseDTO(
                estudiante.getId(),
                estudiante.getNombre(),
                estudiante.getCorreo()
        );
    }
}
