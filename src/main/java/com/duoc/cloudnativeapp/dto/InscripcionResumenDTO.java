package com.duoc.cloudnativeapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InscripcionResumenDTO {

    private Long inscripcionId;
    private String estudiante;
    private LocalDate fechaInscripcion;
    private List<DetalleInscripcionResumenDTO> cursos;
    private BigDecimal totalPagar;

    public InscripcionResumenDTO() {
    }

    public InscripcionResumenDTO(Long inscripcionId,
                                 String estudiante,
                                 LocalDate fechaInscripcion,
                                 List<DetalleInscripcionResumenDTO> cursos,
                                 BigDecimal totalPagar) {
        this.inscripcionId = inscripcionId;
        this.estudiante = estudiante;
        this.fechaInscripcion = fechaInscripcion;
        this.cursos = cursos;
        this.totalPagar = totalPagar;
    }

    public Long getInscripcionId() {
        return inscripcionId;
    }

    public void setInscripcionId(Long inscripcionId) {
        this.inscripcionId = inscripcionId;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public List<DetalleInscripcionResumenDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<DetalleInscripcionResumenDTO> cursos) {
        this.cursos = cursos;
    }

    public BigDecimal getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(BigDecimal totalPagar) {
        this.totalPagar = totalPagar;
    }
}
