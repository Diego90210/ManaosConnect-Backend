package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.*;
import com.diego.gestorcasino.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cajero")
public class CajeroOperacionesController {

    @Autowired
    private ConsumoService consumoService;
    
    @Autowired
    private PlatoService platoService;
    
    @Autowired
    private EmpresaClienteService empresaService;
    
    @Autowired
    private ConsumidorService consumidorService;

    // ⭐ GESTIÓN DE CONSUMOS (Cajero puede hacer todo)
    @PostMapping("/consumos")
    public ResponseEntity<Consumo> registrarConsumo(@RequestBody Consumo consumo) {
        try {
            Consumo nuevo = consumoService.guardar(consumo);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/consumos/{id}")
    public ResponseEntity<Consumo> actualizarConsumo(@PathVariable int id, @RequestBody Consumo consumo) {
        try {
            Consumo actualizado = consumoService.actualizar(id, consumo);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/consumos/{id}")
    public ResponseEntity<String> eliminarConsumo(@PathVariable int id) {
        try {
            consumoService.eliminar(id);
            return ResponseEntity.ok("Consumo eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/consumos")
    public ResponseEntity<List<Consumo>> listarMisConsumos() {
        // Opcional: filtrar por cajero que los creó
        return ResponseEntity.ok(consumoService.listarTodos());
    }

    @GetMapping("/consumos/{id}")
    public ResponseEntity<Consumo> obtenerConsumo(@PathVariable int id) {
        return consumoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado"));
    }

    // ⭐ CONSULTA DE DATOS NECESARIOS PARA OPERACIONES (Solo lectura)
    @GetMapping("/platos")
    public ResponseEntity<List<Plato>> listarPlatosDisponibles() {
        return ResponseEntity.ok(platoService.listarTodos());
    }

    @GetMapping("/platos/{id}")
    public ResponseEntity<Plato> obtenerPlato(@PathVariable int id) {
        return platoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaCliente>> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/empresas/{nit}")
    public ResponseEntity<EmpresaCliente> obtenerEmpresa(@PathVariable String nit) {
        return empresaService.buscarPorNit(nit)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    @GetMapping("/consumidores")
    public ResponseEntity<List<Consumidor>> listarConsumidores() {
        return ResponseEntity.ok(consumidorService.listarTodos());
    }

    @GetMapping("/consumidores/{cedula}")
    public ResponseEntity<Consumidor> obtenerConsumidor(@PathVariable String cedula) {
        return consumidorService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado"));
    }

    // ⭐ ENDPOINT ESPECÍFICO PARA EL FLUJO DE REGISTRO DE CONSUMO
    @GetMapping("/consumidores/empresa/{nit}")
    public ResponseEntity<List<Consumidor>> listarConsumidoresPorEmpresa(@PathVariable String nit) {
        return ResponseEntity.ok(consumidorService.listarPorEmpresa(nit));
    }
}