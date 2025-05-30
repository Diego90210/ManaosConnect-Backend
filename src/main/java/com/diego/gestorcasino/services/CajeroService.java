package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Cajero;
import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.CajeroRepository;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CajeroService {

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Cajero guardar(Cajero cajero) {
        String cedula = cajero.getCedula();

        if (cajeroRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un cajero con cédula: " + cedula);
        }

        if (!usuarioRepository.existsById(cedula)) {
            throw new RuntimeException("Primero debes registrar el usuario con cédula: " + cedula);
        }

        return cajeroRepository.save(cajero);
    }

    public Cajero actualizar(String cedula, Cajero actualizado) {
        Cajero cajero = cajeroRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado con cédula: " + cedula));

        cajero.setNombre(actualizado.getNombre());
        cajero.setTelefono(actualizado.getTelefono());

        return cajeroRepository.save(cajero);
    }

    public void eliminar(String cedula) {
        if (!cajeroRepository.existsById(cedula)) {
            throw new RuntimeException("Cajero no encontrado con cédula: " + cedula);
        }
        cajeroRepository.deleteById(cedula);
    }

    public List<Cajero> listarTodos() {
        return cajeroRepository.findAll();
    }

    public Optional<Cajero> buscarPorCedula(String cedula) {
        return cajeroRepository.findById(cedula);
    }

    public void guardarDesdeRegistro(RegistroUsuarioRequest request, Usuario usuario) {
        Cajero cajero = new Cajero();
        cajero.setCedula(usuario.getCedula());
        cajero.setNombre(request.getNombre());
        cajero.setTelefono(request.getTelefono());
        cajero.setUsuario(usuario);

        if (cajeroRepository.existsById(cajero.getCedula())) {
            throw new RuntimeException("Ya existe un cajero con la cédula: " + cajero.getCedula());
        }

        cajeroRepository.save(cajero);
    }

}

