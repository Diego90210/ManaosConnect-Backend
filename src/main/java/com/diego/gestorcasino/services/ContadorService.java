package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Contador;
import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.ContadorRepository;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContadorService {

    @Autowired
    private ContadorRepository contadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Contador guardar(Contador contador) {
        String cedula = contador.getCedula();

        if (contadorRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un contador con cédula: " + cedula);
        }

        //VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        Usuario usuario = usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        return contadorRepository.save(contador);
    }

    public Contador actualizar(String cedula, Contador actualizado) {
        // VERIFICAR QUE EL USUARIO ESTÉ ACTIVO
        usuarioRepository.findActiveByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));

        Contador contador = contadorRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Contador no encontrado con cédula: " + cedula));

        contador.setNombre(actualizado.getNombre());
        contador.setTelefono(actualizado.getTelefono());

        return contadorRepository.save(contador);
    }

    // USAR SOFT DELETE DEL USUARIO
    public void eliminar(String cedula) {
        if (!contadorRepository.existsById(cedula)) {
            throw new RuntimeException("Contador no encontrado con cédula: " + cedula);
        }
        // Usar soft delete del usuario en lugar de eliminar el contador
        usuarioService.eliminar(cedula);
    }

    // FILTRAR SOLO CONTADORES CON USUARIOS ACTIVOS
    public List<Contador> listarTodos() {
        return contadorRepository.findAll().stream()
                .filter(contador -> contador.getUsuario() != null && contador.getUsuario().getActivo())
                .toList();
    }

    public Optional<Contador> buscarPorCedula(String cedula) {
        return contadorRepository.findById(cedula)
                .filter(contador -> contador.getUsuario() != null && contador.getUsuario().getActivo());
    }

    public void guardarDesdeRegistro(RegistroUsuarioRequest request, Usuario usuario) {
        Contador contador = new Contador();
        contador.setCedula(usuario.getCedula());
        contador.setNombre(request.getNombre());
        contador.setTelefono(request.getTelefono());
        contador.setUsuario(usuario);

        if (contadorRepository.existsById(contador.getCedula())) {
            throw new RuntimeException("Ya existe un contador con la cédula: " + contador.getCedula());
        }

        contadorRepository.save(contador);
    }
}