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

    @Autowired
    private UsuarioService usuarioService;

    public Cajero guardar(Cajero cajero) {
        String cedula = cajero.getCedula();

        if (cajeroRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un cajero con cédula: " + cedula);
        }

        // VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        Usuario usuario = usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        return cajeroRepository.save(cajero);
    }

    public Cajero actualizar(String cedula, Cajero actualizado) {
        // VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        Cajero cajero = cajeroRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado con cédula: " + cedula));

        cajero.setNombre(actualizado.getNombre());
        cajero.setTelefono(actualizado.getTelefono());

        return cajeroRepository.save(cajero);
    }

    // ⭐ USAR SOFT DELETE DEL USUARIO
    public void eliminar(String cedula) {
        if (!cajeroRepository.existsById(cedula)) {
            throw new RuntimeException("Cajero no encontrado con cédula: " + cedula);
        }
        //Usar soft delete del usuario en lugar de eliminar el cajero
        usuarioService.eliminar(cedula);
    }

    //FILTRAR SOLO CAJEROS CON USUARIOS ACTIVOS
    public List<Cajero> listarTodos() {
        return cajeroRepository.findAll().stream()
                .filter(cajero -> cajero.getUsuario() != null && cajero.getUsuario().getActivo())
                .toList();
    }

    public Optional<Cajero> buscarPorCedula(String cedula) {
        return cajeroRepository.findById(cedula)
                .filter(cajero -> cajero.getUsuario() != null && cajero.getUsuario().getActivo());
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