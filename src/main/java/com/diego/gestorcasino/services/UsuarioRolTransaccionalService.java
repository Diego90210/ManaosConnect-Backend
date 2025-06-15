package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.dto.UsuarioResponseDTO;
import com.diego.gestorcasino.dto.UsuarioUpdateRequestDTO;
import com.diego.gestorcasino.models.*;
import com.diego.gestorcasino.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private UsuarioService usuarioService;


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

    // ACTUALIZADO PARA SOFT DELETE
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

    public List<UsuarioResponseDTO> obtenerTodosUsuariosConRol() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioResponseDTO> resultado = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            if (!usuario.getActivo()) continue;

            String cedula = usuario.getCedula();
            String email = usuario.getEmail();
            String rol = usuario.getRol().name();
            String nombre = "";
            String telefono = "";

            switch (usuario.getRol()) {
                case ADMIN -> administradorRepository.findByUsuario(usuario).ifPresent(admin -> {
                    resultado.add(new UsuarioResponseDTO(cedula, admin.getNombre(), admin.getTelefono(), email, rol));
                });
                case CAJERO -> cajeroRepository.findByUsuario(usuario).ifPresent(cajero -> {
                    resultado.add(new UsuarioResponseDTO(cedula, cajero.getNombre(), cajero.getTelefono(), email, rol));
                });
                case CONTADOR -> contadorRepository.findByUsuario(usuario).ifPresent(contador -> {
                    resultado.add(new UsuarioResponseDTO(cedula, contador.getNombre(), contador.getTelefono(), email, rol));
                });
            }
        }

        return resultado;
    }



    @Transactional
    public void reactivarUsuario(String cedula) {
        Usuario usuario = usuarioRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));

        if (usuario.getActivo()) {
            throw new RuntimeException("El usuario ya está activo");
        }

        // Verificar que el email no esté en uso por otro usuario activo (evitar duplicidad)
        usuarioRepository.findActiveByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Ya existe un usuario activo con el email: " + usuario.getEmail());
        });

        usuario.setActivo(true);
        usuario.setFechaEliminacion(null);
        usuario.setEliminadoPor(null);

        usuarioRepository.save(usuario);
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

    //  ACTUALIZADO PARA VALIDAR SOLO USUARIOS ACTIVOS
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
        usuario.setActivo(true); //  NUEVO USUARIO SIEMPRE ACTIVO
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

    public Usuario actualizarUsuario(String cedula, UsuarioUpdateRequestDTO datosActualizados) {
        Usuario usuario = usuarioRepository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));

        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuario.getEmail().equalsIgnoreCase(datosActualizados.getEmail())) {
            if (usuarioRepository.existsByEmail(datosActualizados.getEmail())) {
                throw new RuntimeException("El correo electrónico ya está en uso por otro usuario");
            }
            usuario.setEmail(datosActualizados.getEmail());
        }

        // Actualizar contraseña si viene presente
        if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(datosActualizados.getPassword()));
        }

        usuarioRepository.save(usuario);

        // Actualizar datos en la subentidad correspondiente
        switch (usuario.getRol()) {
            case ADMIN -> {
                Administrador admin = administradorRepository.findByCedula(cedula)
                        .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
                admin.setNombre(datosActualizados.getNombre());
                admin.setTelefono(datosActualizados.getTelefono());
                administradorRepository.save(admin);
            }
            case CAJERO -> {
                Cajero cajero = cajeroRepository.findByCedula(cedula)
                        .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));
                cajero.setNombre(datosActualizados.getNombre());
                cajero.setTelefono(datosActualizados.getTelefono());
                cajeroRepository.save(cajero);
            }
            case CONTADOR -> {
                Contador contador = contadorRepository.findByCedula(cedula)
                        .orElseThrow(() -> new RuntimeException("Contador no encontrado"));
                contador.setNombre(datosActualizados.getNombre());
                contador.setTelefono(datosActualizados.getTelefono());
                contadorRepository.save(contador);
            }
            default -> throw new RuntimeException("Rol no soportado para actualización");
        }

        return usuario;
    }

    public List<UsuarioResponseDTO> listarUsuariosDesactivados() {
        List<Usuario> usuariosInactivos = usuarioRepository.findByActivoFalse();

        return usuariosInactivos.stream().map(usuario -> {
            String nombre = "";
            String telefono = "";

            switch (usuario.getRol()) {
                case ADMIN -> {
                    Administrador admin = administradorRepository.findByCedula(usuario.getCedula())
                            .orElse(null);
                    if (admin != null) {
                        nombre = admin.getNombre();
                        telefono = admin.getTelefono();
                    }
                }
                case CAJERO -> {
                    Cajero cajero = cajeroRepository.findByCedula(usuario.getCedula())
                            .orElse(null);
                    if (cajero != null) {
                        nombre = cajero.getNombre();
                        telefono = cajero.getTelefono();
                    }
                }
                case CONTADOR -> {
                    Contador contador = contadorRepository.findByCedula(usuario.getCedula())
                            .orElse(null);
                    if (contador != null) {
                        nombre = contador.getNombre();
                        telefono = contador.getTelefono();
                    }
                }
            }

            return new UsuarioResponseDTO(
                    usuario.getCedula(),
                    nombre,
                    telefono,
                    usuario.getEmail(),
                    usuario.getRol().name()
            );
        }).toList();
    }


}