package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
}

