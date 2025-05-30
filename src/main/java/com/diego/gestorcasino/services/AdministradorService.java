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

    public Administrador guardar(Administrador administrador) {
        String cedula = administrador.getCedula();

        if (administradorRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un administrador con cédula: " + cedula);
        }

        if (!usuarioRepository.existsById(cedula)) {
            throw new RuntimeException("Primero debes registrar el usuario con cédula: " + cedula);
        }

        return administradorRepository.save(administrador);
    }

    public Administrador actualizar(String cedula, Administrador actualizado) {
        Administrador admin = administradorRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con cédula: " + cedula));

        admin.setNombre(actualizado.getNombre());
        admin.setTelefono(actualizado.getTelefono());

        return administradorRepository.save(admin);
    }

    public void eliminar(String cedula) {
        if (!administradorRepository.existsById(cedula)) {
            throw new RuntimeException("Administrador no encontrado con cédula: " + cedula);
        }
        administradorRepository.deleteById(cedula);
    }

    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    public Optional<Administrador> buscarPorCedula(String cedula) {
        return administradorRepository.findById(cedula);
    }

    public void guardarDesdeRegistro(RegistroUsuarioRequest request, Usuario usuario) {
        Administrador administrador = new Administrador();
        administrador.setUsuario(usuario);

        administrador.setNombre(request.getNombre());
        administrador.setTelefono(request.getTelefono());


        administradorRepository.save(administrador);
    }

}
