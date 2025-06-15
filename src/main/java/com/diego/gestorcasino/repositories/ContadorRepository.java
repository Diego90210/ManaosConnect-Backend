package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Contador;
import com.diego.gestorcasino.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContadorRepository extends JpaRepository<Contador, String> {
    Optional<Contador> findByUsuario(Usuario usuario);

    Optional<Contador> findByCedula(String cedula);
}

