package com.duoc.cloudnativeapp.dto;

import java.math.BigDecimal;

public class DetalleInscripcionResumenDTO {

    private Long cursoId;
    private String nombre;
    private BigDecimal costo;

    public DetalleInscripcionResumenDTO() {
    }

    public DetalleInscripcionResumenDTO(Long cursoId, String nombre, BigDecimal costo) {
        this.cursoId = cursoId;
        this.nombre = nombre;
        this.costo = costo;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}
