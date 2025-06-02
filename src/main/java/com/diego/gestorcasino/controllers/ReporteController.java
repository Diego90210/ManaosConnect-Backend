package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.ReporteRequestDTO;
import com.diego.gestorcasino.dto.ReporteResponseDTO;
import com.diego.gestorcasino.models.Reporte;
import com.diego.gestorcasino.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")  // ⭐ Cambiar a API genérica
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // ⭐ ENDPOINTS SOLO PARA USO INTERNO O API EXTERNA
    @GetMapping
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteService.listarTodos();
    }

    @GetMapping("/empresa/{nit}")
    public List<ReporteResponseDTO> obtenerReportesPorEmpresa(@PathVariable String nit) {
        return reporteService.listarPorEmpresaDTO(nit); // ✅ Funcionará correctamente
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerReportePorId(@PathVariable int id) {
        return reporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));
    }

    //Los endpoints de creación y eliminación están en ContadorReportesController
    // Este controller se mantiene para compatibilidad con APIs externas
}