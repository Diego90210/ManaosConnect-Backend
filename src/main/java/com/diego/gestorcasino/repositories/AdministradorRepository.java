package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, String> {
}

