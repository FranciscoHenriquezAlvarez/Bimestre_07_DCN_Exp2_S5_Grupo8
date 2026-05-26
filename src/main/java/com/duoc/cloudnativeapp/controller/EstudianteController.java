package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.dto.EstudianteRequestDTO;
import com.duoc.cloudnativeapp.dto.EstudianteResponseDTO;
import com.duoc.cloudnativeapp.service.EstudianteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    // Inyeccion del servicio por constructor
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> listar() {
        return ResponseEntity.ok(estudianteService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> crear(@Valid @RequestBody EstudianteRequestDTO estudianteRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(estudianteService.guardar(estudianteRequestDTO));
    }
}
