package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository platoRepository;

    // Obtener todos los platos
    public List<Plato> obtenerTodosLosPlatos() {
        return platoRepository.findAll();
    }

    // Obtener un plato por su ID
    public Plato obtenerPlatoPorId(Long id) {
        return platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + id));
    }

    //Obtener un plato por su nombre
    public Plato obtenerPlatoPorNombre(String nombre){
        return platoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + nombre));
    }

    // Añadir un nuevo plato
    public Plato anadirPlato(Plato plato) {
        return platoRepository.save(plato);
    }

    // Actualizar un plato existente
    public Plato actualizarPlato(Long id, Plato detallesPlato) {
        Plato platoExistente = platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + id));

        platoExistente.setNombre(detallesPlato.getNombre());
        platoExistente.setPrecio(detallesPlato.getPrecio());
        platoExistente.setDescripcion(detallesPlato.getDescripcion());
        platoExistente.setCategoria(detallesPlato.getCategoria());  // Añadido para manejar la categoría

        return platoRepository.save(platoExistente);
    }

    //Actualizar un plato existente mediante su nombre
    public Plato actualizarPlatoPorNombre (String nombre, Plato detallesPlato){
        Plato platoExistente = platoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + nombre));

        platoExistente.setNombre(detallesPlato.getNombre());
        platoExistente.setPrecio(detallesPlato.getPrecio());
        platoExistente.setDescripcion(detallesPlato.getDescripcion());
        platoExistente.setCategoria(detallesPlato.getCategoria());

        return platoRepository.save(platoExistente);
    }

    // Eliminar un plato
    public void eliminarPlato(Long id) {
        Plato plato = platoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con id: " + id));
        platoRepository.delete(plato);
    }

    //Eliminar un plato mediante su nombre
    public void eliminarPlatoPorNombre(String nombre){
        Plato plato = platoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + nombre));
        platoRepository.delete(plato);
    }
}



