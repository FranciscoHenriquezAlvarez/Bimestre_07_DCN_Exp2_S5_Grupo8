package com.duoc.cloudnativeapp.service;

import com.duoc.cloudnativeapp.dto.CursoRequestDTO;
import com.duoc.cloudnativeapp.dto.CursoResponseDTO;
import com.duoc.cloudnativeapp.model.Curso;
import com.duoc.cloudnativeapp.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio con la logica simple de cursos
@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    // Inyeccion del repositorio por constructor
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    // Busca todos los cursos disponibles
    public List<CursoResponseDTO> obtenerTodos() {
        return cursoRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }

    public CursoResponseDTO guardar(CursoRequestDTO cursoRequestDTO) {
        Curso curso = new Curso();
        curso.setNombre(cursoRequestDTO.getNombre());
        curso.setInstructor(cursoRequestDTO.getInstructor());
        curso.setDuracionHoras(cursoRequestDTO.getDuracionHoras());
        curso.setCosto(cursoRequestDTO.getCosto());

        return convertirAResponseDTO(cursoRepository.save(curso));
    }

    // Convierte la entidad a DTO para entregar una respuesta simple
    private CursoResponseDTO convertirAResponseDTO(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getNombre(),
                curso.getInstructor(),
                curso.getDuracionHoras(),
                curso.getCosto()
        );
    }
}
