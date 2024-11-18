package com.diego.gestorcasino.controllers;


import com.diego.gestorcasino.models.Empresa;
import com.diego.gestorcasino.services.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    // Obtener todas las empresas
    @GetMapping
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaService.obtenerTodasLasEmpresas();
    }

    // Obtener una empresa por NIT
    @GetMapping("/{nit}")
    public ResponseEntity<Empresa> obtenerEmpresaPorNit(@PathVariable String nit) {
        Empresa empresa = empresaService.obtenerEmpresaPorNit(nit);
        return ResponseEntity.ok(empresa);
    }

    // anadir una nueva empresa
    @PostMapping
    public ResponseEntity<Empresa> anadirEmpresa(@RequestBody Empresa empresa) {
        Empresa nuevaEmpresa = empresaService.anadirEmpresa(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
    }

    // Actualizar una empresa existente
    @PutMapping("/{nit}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable String nit, @RequestBody Empresa detallesEmpresa) {
        Empresa empresaActualizada = empresaService.actualizarEmpresa(nit, detallesEmpresa);
        return ResponseEntity.ok(empresaActualizada);
    }

    // Eliminar una empresa
    @DeleteMapping("/{nit}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable String nit) {
        empresaService.eliminarEmpresa(nit);
        return ResponseEntity.noContent().build();
    }
}

