package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.repositories.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    public Administrador crearAdministrador(Administrador administrador) {
        // Validar si el email ya existe
        if (administradorRepository.findByEmail(administrador.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }
        return administradorRepository.save(administrador);
    }

    public Optional<Administrador> obtenerAdministradorPorEmail(String email) {
        return administradorRepository.findByEmail(email);
    }

    public boolean verificarCredenciales(String email, String password) {
        Optional<Administrador> administrador = administradorRepository.findByEmail(email);
        return administrador.isPresent() && administrador.get().getPassword().equals(password);
    }
}
