package com.duoc.cloudnativeapp.controller;

import com.duoc.cloudnativeapp.dto.InscripcionRequestDTO;
import com.duoc.cloudnativeapp.dto.InscripcionResumenDTO;
import com.duoc.cloudnativeapp.service.InscripcionService;
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
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    // Inyeccion del servicio por constructor
    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    public ResponseEntity<List<InscripcionResumenDTO>> listar() {
        return ResponseEntity.ok(inscripcionService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<InscripcionResumenDTO> crear(@Valid @RequestBody InscripcionRequestDTO inscripcionRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.guardar(inscripcionRequestDTO));
    }
}
