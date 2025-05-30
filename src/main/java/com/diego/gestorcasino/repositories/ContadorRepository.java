package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Contador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContadorRepository extends JpaRepository<Contador, String> {
}

