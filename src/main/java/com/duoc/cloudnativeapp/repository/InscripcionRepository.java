package com.duoc.cloudnativeapp.repository;

import com.duoc.cloudnativeapp.model.Inscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Carga la inscripcion junto con estudiante, detalles y cursos para armar el resumen completo.
    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    List<Inscripcion> findAllByOrderByIdAsc();

    // Permite obtener una inscripcion completa al consultar por su identificador.
    @Override
    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    Optional<Inscripcion> findById(Long id);
}
