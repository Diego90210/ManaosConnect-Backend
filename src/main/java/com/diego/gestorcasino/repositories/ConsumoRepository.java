package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Consumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsumoRepository extends JpaRepository<Consumo, Integer> {
    List<Consumo> findByCedulaEmpleado(String cedula);
}

