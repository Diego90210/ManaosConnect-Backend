package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getCedula())) {
            throw new RuntimeException("Ya existe un usuario con la cédula: " + usuario.getCedula());
        }
        
        // VERIFICAR EMAIL SOLO EN USUARIOS ACTIVOS
        if (usuarioRepository.findActiveByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario activo con el email: " + usuario.getEmail());
        }
        
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true); // NUEVO USUARIO SIEMPRE ACTIVO
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(String cedula, Usuario actualizado) {
        Usuario usuario = usuarioRepository.findActiveByCedula(cedula) // SOLO ACTIVOS
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        usuario.setEmail(actualizado.getEmail());
        usuario.setPassword(passwordEncoder.encode(actualizado.getPassword()));
        usuario.setRol(actualizado.getRol());

        return usuarioRepository.save(usuario);
    }

    //SOFT DELETE
    public void eliminar(String cedula) {
        Usuario usuario = usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));
        
        // Marcar como eliminado
        usuario.setActivo(false);
        usuario.setFechaEliminacion(LocalDateTime.now());
        
        // Obtener quien elimina desde el contexto de seguridad
        String eliminadoPor = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        usuario.setEliminadoPor(eliminadoPor);
        
        usuarioRepository.save(usuario);
    }

    public void reactivarUsuario(String cedula) {
        Usuario usuario = usuarioRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
        
        if (usuario.getActivo()) {
            throw new RuntimeException("El usuario ya está activo");
        }
        
        // Verificar que el email no esté en uso por otro usuario activo
        if (usuarioRepository.findActiveByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario activo con el email: " + usuario.getEmail());
        }
        
        usuario.setActivo(true);
        usuario.setFechaEliminacion(null);
        usuario.setEliminadoPor(null);
        
        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorCedula(String cedula) {
        return usuarioRepository.findActiveByCedula(cedula);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllActive();
    }

    public List<Usuario> listarEliminados() {
        return usuarioRepository.findAllInactive();
    }

    public List<Usuario> listarTodosIncluyendoEliminados() {
        return usuarioRepository.findAll();
    }
}