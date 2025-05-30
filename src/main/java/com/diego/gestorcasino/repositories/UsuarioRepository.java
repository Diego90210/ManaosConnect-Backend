package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Rol;
import com.diego.gestorcasino.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByCedula(String cedula);
    Optional<Usuario> findByEmail(String email);
    long countByRol(Rol rol);
}

