package com.duoc.cloudnativeapp.dto;

public class ResumenInscripcionMensaje {

    private Long inscripcionId;
    private String estudiante;
    private String curso;
    private String fechaInscripcion;
    private Double totalPagar;
    private String contenidoResumen;

    public ResumenInscripcionMensaje() {
    }

    public ResumenInscripcionMensaje(Long inscripcionId, String estudiante, String curso,
                                     String fechaInscripcion, Double totalPagar, String contenidoResumen) {
        this.inscripcionId = inscripcionId;
        this.estudiante = estudiante;
        this.curso = curso;
        this.fechaInscripcion = fechaInscripcion;
        this.totalPagar = totalPagar;
        this.contenidoResumen = contenidoResumen;
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

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(String fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Double getTotalPagar() {
        return totalPagar;
    }

    public void setTotalPagar(Double totalPagar) {
        this.totalPagar = totalPagar;
    }

    public String getContenidoResumen() {
        return contenidoResumen;
    }

    public void setContenidoResumen(String contenidoResumen) {
        this.contenidoResumen = contenidoResumen;
    }
}
