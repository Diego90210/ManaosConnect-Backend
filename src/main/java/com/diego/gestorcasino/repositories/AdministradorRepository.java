package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    boolean existsByEmail(String email);
    Optional<Administrador> findByEmail(String email);
}

