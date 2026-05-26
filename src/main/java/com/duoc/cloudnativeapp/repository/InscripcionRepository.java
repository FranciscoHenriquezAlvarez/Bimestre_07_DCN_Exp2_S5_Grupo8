package com.duoc.cloudnativeapp.repository;

import com.duoc.cloudnativeapp.model.Inscripcion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    @EntityGraph(attributePaths = {"estudiante", "detalles", "detalles.curso"})
    List<Inscripcion> findAllByOrderByIdAsc();
}
