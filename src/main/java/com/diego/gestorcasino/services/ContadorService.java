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

    public Contador guardar(Contador contador) {
        String cedula = contador.getCedula();

        if (contadorRepository.existsById(cedula)) {
            throw new RuntimeException("Ya existe un contador con cédula: " + cedula);
        }

        if (!usuarioRepository.existsById(cedula)) {
            throw new RuntimeException("Primero debes registrar el usuario con cédula: " + cedula);
        }

        return contadorRepository.save(contador);
    }

    public Contador actualizar(String cedula, Contador actualizado) {
        Contador contador = contadorRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Contador no encontrado con cédula: " + cedula));

        contador.setNombre(actualizado.getNombre());
        contador.setTelefono(actualizado.getTelefono());

        return contadorRepository.save(contador);
    }

    public void eliminar(String cedula) {
        if (!contadorRepository.existsById(cedula)) {
            throw new RuntimeException("Contador no encontrado con cédula: " + cedula);
        }
        contadorRepository.deleteById(cedula);
    }

    public List<Contador> listarTodos() {
        return contadorRepository.findAll();
    }

    public Optional<Contador> buscarPorCedula(String cedula) {
        return contadorRepository.findById(cedula);
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

