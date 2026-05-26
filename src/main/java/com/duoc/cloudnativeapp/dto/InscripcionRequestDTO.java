package com.duoc.cloudnativeapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class InscripcionRequestDTO {

    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El estudiante debe ser positivo")
    private Long estudianteId;

    @NotEmpty(message = "Debe enviar al menos un curso")
    private List<@NotNull(message = "El id del curso es obligatorio")
            @Positive(message = "El id del curso debe ser positivo") Long> cursosIds;

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public List<Long> getCursosIds() {
        return cursosIds;
    }

    public void setCursosIds(List<Long> cursosIds) {
        this.cursosIds = cursosIds;
    }
}
