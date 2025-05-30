package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Cajero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CajeroRepository extends JpaRepository<Cajero, String> {
}

