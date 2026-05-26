package com.duoc.cloudnativeapp.repository;

import com.duoc.cloudnativeapp.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    List<Curso> findAllByOrderByIdAsc();
}
