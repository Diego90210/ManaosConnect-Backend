package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.services.ConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoService consumoService;

    // Obtener todos los consumos
    @GetMapping
    public ResponseEntity<List<Consumo>> obtenerTodosLosConsumos() {
        List<Consumo> consumos = consumoService.obtenerTodosLosConsumos();
        return ResponseEntity.ok(consumos);
    }

    // Obtener consumos por empleado
    @GetMapping("/empleado/{cedula}")
    public ResponseEntity<List<Consumo>> obtenerConsumosPorEmpleado(@PathVariable String cedula) {
        List<Consumo> consumos = consumoService.obtenerConsumosPorEmpleado(cedula);
        return ResponseEntity.ok(consumos);
    }

    // Obtener un consumo por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Consumo> obtenerConsumoPorId(@PathVariable int id) {
        Consumo consumo = consumoService.obtenerConsumoPorId(id);
        return ResponseEntity.ok(consumo);
    }

    // Crear un nuevo consumo
    @PostMapping
    public ResponseEntity<Consumo> anadirConsumo(@RequestBody Consumo consumo) {
        Consumo nuevoConsumo = consumoService.anadirConsumo(consumo.getCedulaEmpleado(),consumo);
        return ResponseEntity.ok(nuevoConsumo);
    }

    // Actualizar un consumo
    @PutMapping("/{id}")
    public ResponseEntity<Consumo> actualizarConsumo(@PathVariable int id, @RequestBody Consumo detallesConsumo) {
        Consumo consumoActualizado = consumoService.actualizarConsumo(id, detallesConsumo);
        return ResponseEntity.ok(consumoActualizado);
    }

    // Eliminar un consumo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarConsumo(@PathVariable int id) {
        consumoService.eliminarConsumo(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener el total de un consumo (opcional, ya que el total se encuentra dentro del objeto)
    @GetMapping("/{id}/total")
    public ResponseEntity<Double> obtenerTotalConsumo(@PathVariable int id) {
        Consumo consumo = consumoService.obtenerConsumoPorId(id);
        return ResponseEntity.ok(consumo.getTotal());
    }
}
