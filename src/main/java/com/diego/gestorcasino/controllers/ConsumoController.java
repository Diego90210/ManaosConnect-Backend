package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.ConsumoDTO;
import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.services.ConsumoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    @Autowired
    private ConsumoService consumoService;

    // Obtener todos los consumos
    @GetMapping
    public ResponseEntity<List<ConsumoDTO>> obtenerTodosLosConsumos() {
        List<ConsumoDTO> consumos = consumoService.obtenerTodosLosConsumos();
        return ResponseEntity.ok(consumos);
    }


    @GetMapping("/empleado/{cedula}")
    public ResponseEntity<List<ConsumoDTO>> obtenerConsumosPorEmpleado(@PathVariable String cedula) {
        List<ConsumoDTO> consumosDTO = consumoService.obtenerConsumosPorEmpleado(cedula);
        return ResponseEntity.ok(consumosDTO);
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

    @GetMapping("/{id}/total")
    public ResponseEntity<Double> obtenerTotalConsumo(@PathVariable int id) {
        ConsumoDTO consumoDTO = consumoService.obtenerConsumoPorId(id); // Obtener el DTO
        return ResponseEntity.ok(consumoDTO.getTotal()); // Extraer el total desde el DTO
    }


    @GetMapping("/{id}")
    public ResponseEntity<ConsumoDTO> obtenerConsumoPorId(@PathVariable int id) {
        ConsumoDTO consumoDTO = consumoService.obtenerConsumoPorId(id);
        return ResponseEntity.ok(consumoDTO);
    }


}
