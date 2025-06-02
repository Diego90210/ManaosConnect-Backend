package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    public Plato guardar(Plato plato) {
        // Validar que no exista un plato con el mismo nombre
        if (platoRepository.findByNombreIgnoreCase(plato.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un plato con el nombre: " + plato.getNombre());
        }
        
        // Validar precio válido
        if (plato.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }
        
        return platoRepository.save(plato);
    }

    public Plato actualizar(int id, Plato detallesPlato) {
        Plato platoExistente = platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + id));

        // ⭐ SOLUCIÓN: Usar == para comparar primitivos int
        Optional<Plato> platoConMismoNombre = platoRepository.findByNombreIgnoreCase(detallesPlato.getNombre());
        if (platoConMismoNombre.isPresent() && platoConMismoNombre.get().getId() != id) {
            throw new RuntimeException("Ya existe otro plato con el nombre: " + detallesPlato.getNombre());
        }

        // Validar precio válido
        if (detallesPlato.getPrecio() <= 0) {
            throw new RuntimeException("El precio debe ser mayor a 0");
        }

        platoExistente.setNombre(detallesPlato.getNombre());
        platoExistente.setPrecio(detallesPlato.getPrecio());
        platoExistente.setDescripcion(detallesPlato.getDescripcion());
        
        // Añadir categoría si está disponible
        if (detallesPlato.getCategoria() != null) {
            platoExistente.setCategoria(detallesPlato.getCategoria());
        }

        return platoRepository.save(platoExistente);
    }

    public void eliminar(int id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + id));
        platoRepository.delete(plato);
    }

    public List<Plato> listarTodos() {
        return platoRepository.findAll();
    }

    public Optional<Plato> buscarPorId(int id) {
        return platoRepository.findById(id);
    }

    public Optional<Plato> buscarPorNombre(String nombre) {
        return platoRepository.findByNombreIgnoreCase(nombre);
    }

    public Plato obtenerPorId(int id) {
        return buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con ID: " + id));
    }

    public Plato obtenerPorNombre(String nombre) {
        return buscarPorNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + nombre));
    }

    // ⭐ MÉTODOS EXISTENTES PARA MANTENER COMPATIBILIDAD
    public List<Plato> obtenerTodosLosPlatos() {
        return listarTodos();
    }

    public Plato obtenerPlatoPorId(int id) {
        return obtenerPorId(id);
    }

    public Plato obtenerPlatoPorNombre(String nombre) {
        return obtenerPorNombre(nombre);
    }

    public Plato anadirPlato(Plato plato) {
        return guardar(plato);
    }

    public Plato actualizarPlato(int id, Plato detallesPlato) {
        return actualizar(id, detallesPlato);
    }

    public Plato actualizarPlatoPorNombre(String nombre, Plato detallesPlato) {
        Plato platoExistente = obtenerPorNombre(nombre);
        return actualizar(platoExistente.getId(), detallesPlato);
    }

    public void eliminarPlato(int id) {
        eliminar(id);
    }

    public void eliminarPlatoPorNombre(String nombre) {
        Plato plato = obtenerPorNombre(nombre);
        eliminar(plato.getId());
    }
}