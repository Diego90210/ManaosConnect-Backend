package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.AdministradorRepository;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Administrador guardar(Administrador administrador) {
        String cedula = administrador.getCedula();

        if (administradorRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un administrador con cédula: " + cedula);
        }

        // VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        Usuario usuario = usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        return administradorRepository.save(administrador);
    }

    public Administrador actualizar(String cedula, Administrador actualizado) {
        // VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        Administrador admin = administradorRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con cédula: " + cedula));

        admin.setNombre(actualizado.getNombre());
        admin.setTelefono(actualizado.getTelefono());

        return administradorRepository.save(admin);
    }

    // USAR SOFT DELETE DEL USUARIO
    public void eliminar(String cedula) {
        if (!administradorRepository.existsById(cedula)) {
            throw new RuntimeException("Administrador no encontrado con cédula: " + cedula);
        }
        // Usar soft delete del usuario en lugar de eliminar el administrador
        usuarioService.eliminar(cedula);
    }

    // FILTRAR SOLO ADMINISTRADORES CON USUARIOS ACTIVOS
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll().stream()
                .filter(admin -> admin.getUsuario() != null && admin.getUsuario().getActivo())
                .toList();
    }

    public Optional<Administrador> buscarPorCedula(String cedula) {
        return administradorRepository.findById(cedula)
                .filter(admin -> admin.getUsuario() != null && admin.getUsuario().getActivo());
    }

    public void guardarDesdeRegistro(RegistroUsuarioRequest request, Usuario usuario) {
        Administrador administrador = new Administrador();
        administrador.setUsuario(usuario);
        administrador.setNombre(request.getNombre());
        administrador.setTelefono(request.getTelefono());
        administradorRepository.save(administrador);
    }
}