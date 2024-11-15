package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Long> {
    @Query("SELECT p FROM Plato p WHERE LOWER(p.nombre) = LOWER(:nombre)")
    Optional<Plato> findByNombreIgnoreCase(@Param("nombre") String nombre);
}

