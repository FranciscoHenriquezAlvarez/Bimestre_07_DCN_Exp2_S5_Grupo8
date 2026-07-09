package com.duoc.cloudnativeapp.repository;

import com.duoc.cloudnativeapp.model.ResumenInscripcionMq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumenInscripcionMqRepository extends JpaRepository<ResumenInscripcionMq, Long> {

    List<ResumenInscripcionMq> findAllByOrderByIdAsc();
}
