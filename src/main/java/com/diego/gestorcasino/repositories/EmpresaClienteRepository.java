package com.diego.gestorcasino.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaClienteRepository extends JpaRepository<com.diego.gestorcasino.models.EmpresaCliente, Integer> {
    Optional<com.diego.gestorcasino.models.EmpresaCliente> findByNit(String nit);
}
