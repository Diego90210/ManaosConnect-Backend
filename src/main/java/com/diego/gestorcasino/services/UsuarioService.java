package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(String cedula, Usuario actualizado) {
        Usuario usuario = usuarioRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));

        usuario.setEmail(actualizado.getEmail());
        usuario.setPassword(passwordEncoder.encode(actualizado.getPassword()));
        usuario.setRol(actualizado.getRol());

        return usuarioRepository.save(usuario);
    }

    public void eliminar(String cedula) {
        if (!usuarioRepository.existsById(cedula)) {
            throw new RuntimeException("Usuario no encontrado con cédula: " + cedula);
        }
        usuarioRepository.deleteById(cedula);
    }

    public Optional<Usuario> buscarPorCedula(String cedula) {
        return usuarioRepository.findByCedula(cedula);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
