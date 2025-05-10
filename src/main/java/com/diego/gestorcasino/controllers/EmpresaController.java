package com.diego.gestorcasino.controllers;


import com.diego.gestorcasino.models.EmpresaCliente;
import com.diego.gestorcasino.services.EmpresaClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaClienteService empresaClienteService;

    // Obtener todas las empresas
    @GetMapping
    public List<EmpresaCliente> obtenerTodasLasEmpresas() {
        return empresaClienteService.obtenerTodasLasEmpresas();
    }

    // Obtener una empresa por NIT
    @GetMapping("/{nit}")
    public ResponseEntity<EmpresaCliente> obtenerEmpresaPorNit(@PathVariable String nit) {
        EmpresaCliente empresaCliente = empresaClienteService.obtenerEmpresaPorNit(nit);
        return ResponseEntity.ok(empresaCliente);
    }

    // anadir una nueva empresa
    @PostMapping
    public ResponseEntity<EmpresaCliente> anadirEmpresa(@RequestBody EmpresaCliente empresaCliente) {
        EmpresaCliente nuevaEmpresaCliente = empresaClienteService.anadirEmpresa(empresaCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresaCliente);
    }

    // Actualizar una empresa existente
    @PutMapping("/{nit}")
    public ResponseEntity<EmpresaCliente> actualizarEmpresa(@PathVariable String nit, @RequestBody EmpresaCliente detallesEmpresaCliente) {
        EmpresaCliente empresaClienteActualizada = empresaClienteService.actualizarEmpresa(nit, detallesEmpresaCliente);
        return ResponseEntity.ok(empresaClienteActualizada);
    }

    // Eliminar una empresa
    @DeleteMapping("/{nit}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable String nit) {
        empresaClienteService.eliminarEmpresa(nit);
        return ResponseEntity.noContent().build();
    }
}

