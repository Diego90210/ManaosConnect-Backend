package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.*;
import com.diego.gestorcasino.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsuarioRolTransaccionalService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private ContadorRepository contadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuarioCompleto(RegistroUsuarioRequest request) {
        try {
            // Validaciones previas
            validarRequest(request);
            validarDisponibilidad(request);

            // 1. Crear y guardar usuario
            Usuario usuario = crearUsuario(request);
            usuario = usuarioRepository.save(usuario);

            // 2. Crear entidad de rol correspondiente
            crearEntidadRol(request, usuario);

            return usuario;
            
        } catch (Exception e) {
            // La transacción se revierte automáticamente por @Transactional
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage(), e);
        }
    }

    // ⭐ ACTUALIZADO PARA SOFT DELETE
    @Transactional
    public void eliminarUsuarioCompleto(String cedula) {
        try {
            // Buscar el usuario
            Usuario usuario = usuarioRepository.findActiveByCedula(cedula)
                    .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

            // Soft delete del usuario
            usuario.setActivo(false);
            usuario.setFechaEliminacion(LocalDateTime.now());
            
            String eliminadoPor = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            usuario.setEliminadoPor(eliminadoPor);
            
            usuarioRepository.save(usuario);
            
            // Nota: Las entidades de rol mantienen la referencia, pero el usuario está marcado como inactivo
            
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario completo: " + e.getMessage(), e);
        }
    }

    // Métodos privados de apoyo
    private void validarRequest(RegistroUsuarioRequest request) {
        if (request.getCedula() == null || request.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (request.getRol() == null) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
    }

    // ⭐ ACTUALIZADO PARA VALIDAR SOLO USUARIOS ACTIVOS
    private void validarDisponibilidad(RegistroUsuarioRequest request) {
        if (usuarioRepository.existsById(request.getCedula())) {
            throw new RuntimeException("Ya existe un usuario con la cédula: " + request.getCedula());
        }
        
        if (usuarioRepository.findActiveByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario activo con el email: " + request.getEmail());
        }
    }

    private Usuario crearUsuario(RegistroUsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setCedula(request.getCedula());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setActivo(true); // ⭐ NUEVO USUARIO SIEMPRE ACTIVO
        return usuario;
    }

    private void crearEntidadRol(RegistroUsuarioRequest request, Usuario usuario) {
        switch (request.getRol()) {
            case ADMIN -> crearAdministrador(request, usuario);
            case CAJERO -> crearCajero(request, usuario);
            case CONTADOR -> crearContador(request, usuario);
            default -> throw new IllegalArgumentException("Rol no soportado: " + request.getRol());
        }
    }

    private void crearAdministrador(RegistroUsuarioRequest request, Usuario usuario) {
        Administrador admin = new Administrador(usuario);
        admin.setNombre(request.getNombre());
        admin.setTelefono(request.getTelefono());
        administradorRepository.save(admin);
    }

    private void crearCajero(RegistroUsuarioRequest request, Usuario usuario) {
        Cajero cajero = new Cajero(usuario);
        cajero.setNombre(request.getNombre());
        cajero.setTelefono(request.getTelefono());
        cajeroRepository.save(cajero);
    }

    private void crearContador(RegistroUsuarioRequest request, Usuario usuario) {
        Contador contador = new Contador(usuario);
        contador.setNombre(request.getNombre());
        contador.setTelefono(request.getTelefono());
        contadorRepository.save(contador);
    }
}