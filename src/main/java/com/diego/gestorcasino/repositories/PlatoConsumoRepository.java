package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.PlatoConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoConsumoRepository extends JpaRepository<PlatoConsumo, Integer> {
}

