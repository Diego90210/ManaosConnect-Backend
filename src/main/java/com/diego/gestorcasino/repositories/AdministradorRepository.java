package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, String> {
    Optional<Administrador> findByUsuario(Usuario usuario);

    Optional<Administrador> findByCedula(String cedula);
}

