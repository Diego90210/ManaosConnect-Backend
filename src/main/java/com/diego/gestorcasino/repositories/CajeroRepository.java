package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Cajero;
import com.diego.gestorcasino.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CajeroRepository extends JpaRepository<Cajero, String> {
    Optional<Cajero> findByUsuario(Usuario usuario);
    Optional<Cajero> findByCedula(String cedula);
}

