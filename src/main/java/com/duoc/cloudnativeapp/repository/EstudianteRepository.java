package com.duoc.cloudnativeapp.repository;

import com.duoc.cloudnativeapp.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findAllByOrderByIdAsc();
}
