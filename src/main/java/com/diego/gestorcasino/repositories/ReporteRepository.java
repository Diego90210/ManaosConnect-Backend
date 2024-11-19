package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {
    List<Reporte> findByEmpresaNit(String nit);
}

