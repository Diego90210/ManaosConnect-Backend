package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.services.PlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platos")
public class PlatoController {

    @Autowired
    private PlatoService platoService;

    // Obtener todos los platos
    @GetMapping
    public List<Plato> obtenerTodosLosPlatos() {
        return platoService.obtenerTodosLosPlatos();
    }

    // Obtener un plato por su ID
    @GetMapping("id/{id}")
    public ResponseEntity<Plato> obtenerPlatoPorId(@PathVariable int id) {
        Plato plato = platoService.obtenerPlatoPorId(id);
        return ResponseEntity.ok(plato);
    }

    // Obtener un plato por su nombre
    @GetMapping("nombre/{nombre}")
    public ResponseEntity<Plato> obtenerPlatoPorNombre(@PathVariable String nombre){
        Plato plato = platoService.obtenerPlatoPorNombre(nombre);
        return ResponseEntity.ok(plato);
    }

    // Añadir un nuevo plato
    @PostMapping
    public ResponseEntity<Plato> anadirPlato(@RequestBody Plato plato) {
        Plato nuevoPlato = platoService.anadirPlato(plato);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlato);
    }

    // Actualizar un plato existente
    @PutMapping("id/{id}")
    public ResponseEntity<Plato> actualizarPlato(@PathVariable int id, @RequestBody Plato detallesPlato) {
        Plato platoActualizado = platoService.actualizarPlato(id, detallesPlato);
        return ResponseEntity.ok(platoActualizado);
    }

    // Actualizar un plato existente mediante su nombre
    @PutMapping("nombre/{nombre}")
    public ResponseEntity<Plato> actualizarPlatoPorNombre (@PathVariable String nombre, @RequestBody Plato detallesPlato){
        Plato platoActualizado = platoService.actualizarPlatoPorNombre(nombre, detallesPlato);
        return ResponseEntity.ok(platoActualizado);
    }
    // Eliminar un plato
    @DeleteMapping("id/{id}")
    public ResponseEntity<Void> eliminarPlato(@PathVariable int id) {
        platoService.eliminarPlato(id);
        return ResponseEntity.noContent().build();
    }

    // Eliminar un plato mediante su nombre
    @DeleteMapping("nombre/{nombre}")
    public ResponseEntity<Void> eliminarPlatoPorNombre(@PathVariable String nombre){
        platoService.eliminarPlatoPorNombre(nombre);
        return ResponseEntity.noContent().build();
    }
}
