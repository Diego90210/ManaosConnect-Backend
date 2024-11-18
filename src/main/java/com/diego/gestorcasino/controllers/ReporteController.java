package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Reporte;
import com.diego.gestorcasino.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Obtener todos los reportes
    @GetMapping
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteService.obtenerTodosLosReportes();
    }

    // Obtener reportes por empresa (por NIT)
    @GetMapping("/empresa/{nit}")
    public List<Reporte> obtenerReportesPorEmpresa(@PathVariable Long nit) {
        return reporteService.obtenerReportesPorEmpresa(nit);
    }

    // Obtener un reporte por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReportePorId(@PathVariable Long id) {
        Reporte reporte = reporteService.obtenerReportePorId(id);
        return ResponseEntity.ok(reporte);
    }

    // Crear un nuevo reporte
    @PostMapping
    public ResponseEntity<Reporte> crearReporte(@RequestParam String nitEmpresa,
                                                @RequestParam LocalDate fechaInicio,
                                                @RequestParam LocalDate fechaFin) {
        Reporte nuevoReporte = reporteService.crearReporte(nitEmpresa, fechaInicio, fechaFin);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoReporte);
    }

    // Eliminar un reporte
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        reporteService.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }
}


