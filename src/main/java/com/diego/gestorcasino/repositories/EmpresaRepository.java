package com.diego.gestorcasino.repositories;

import com.diego.gestorcasino.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByNit(String nit);
}
