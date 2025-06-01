package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Rol;
import com.diego.gestorcasino.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.activo = true")
    List<Usuario> findAllActive();
    
    @Query("SELECT u FROM Usuario u WHERE u.cedula = :cedula AND u.activo = true")
    Optional<Usuario> findActiveByCedula(@Param("cedula") String cedula);
    
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.activo = true")
    Optional<Usuario> findActiveByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol AND u.activo = true")
    long countActiveByRol(@Param("rol") Rol rol);

    @Query("SELECT u FROM Usuario u WHERE u.activo = false")
    List<Usuario> findAllInactive();

    Optional<Usuario> findByCedula(String cedula);
    Optional<Usuario> findByEmail(String email);
    long countByRol(Rol rol);
}