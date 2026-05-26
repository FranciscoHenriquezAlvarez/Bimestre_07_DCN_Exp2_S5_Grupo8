package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.dto.CursoRequestDTO;
import com.duoc.cloudnativeapp.dto.CursoResponseDTO;
import com.duoc.cloudnativeapp.service.CursoService;
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
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    // Inyeccion del servicio por constructor
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listar() {
        return ResponseEntity.ok(cursoService.obtenerTodos());
    }

    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(@Valid @RequestBody CursoRequestDTO cursoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.guardar(cursoRequestDTO));
    }
}
