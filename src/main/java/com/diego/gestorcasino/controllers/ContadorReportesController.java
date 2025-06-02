package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.ConsumoDTO;
import com.diego.gestorcasino.dto.ReporteRequestDTO;
import com.diego.gestorcasino.dto.ReporteResponseDTO;
import com.diego.gestorcasino.models.*;
import com.diego.gestorcasino.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/contador")
public class ContadorReportesController {

    @Autowired
    private ReporteService reporteService;
    
    @Autowired
    private ConsumoService consumoService;
    
    @Autowired
    private EmpresaClienteService empresaService;

    // ⭐ CREAR reporte (mantener entity porque es creación)
    @PostMapping("/reportes")
    public ResponseEntity<ReporteResponseDTO> crearReporte(@RequestBody ReporteRequestDTO requestDTO) {
        try {
            Reporte nuevo = reporteService.crear(requestDTO);
            ReporteResponseDTO dto = reporteService.convertirADTO(nuevo); // ⭐ Convertir a DTO
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/reportes/generar")
    public ResponseEntity<ReporteResponseDTO> generarReporteDirecto(
            @RequestParam String nitEmpresa,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            Reporte reporte = reporteService.crearReporte(nitEmpresa, fechaInicio, fechaFin);
            ReporteResponseDTO dto = reporteService.convertirADTO(reporte); // ⭐ Convertir a DTO
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/reportes/{id}")
    public ResponseEntity<String> eliminarReporte(@PathVariable int id) {
        try {
            reporteService.eliminar(id);
            return ResponseEntity.ok("Reporte eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ⭐ CAMBIAR A DTOs PARA CONSULTAS
    @GetMapping("/reportes")
    public ResponseEntity<List<ReporteResponseDTO>> listarTodosReportes() {
        return ResponseEntity.ok(reporteService.listarTodosDTO()); // ⭐ Cambio aquí
    }

    @GetMapping("/reportes/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerReporte(@PathVariable int id) {
        ReporteResponseDTO dto = reporteService.obtenerPorIdDTO(id); // ⭐ Cambio aquí
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/reportes/empresa/{nit}")
    public ResponseEntity<List<ReporteResponseDTO>> listarReportesPorEmpresa(@PathVariable String nit) {
        return ResponseEntity.ok(reporteService.listarPorEmpresaDTO(nit)); // ✅ Funcionará correctamente
    }

    // ⭐ Los endpoints de consumos y empresas también pueden necesitar DTOs
    @GetMapping("/consumos")
    public ResponseEntity<List<ConsumoDTO>> listarTodosConsumos() {
        return ResponseEntity.ok(consumoService.obtenerTodosLosConsumos()); // Si ya usas DTOs
    }

    // ⭐ ACTUALIZAR ResumenEmpresaDTO para usar DTOs
    @GetMapping("/estadisticas/resumen-empresa/{nit}")
    public ResponseEntity<ResumenEmpresaDTO> obtenerResumenEmpresa(@PathVariable String nit) {
        try {
            // Obtener reportes como DTOs
            List<ReporteResponseDTO> reportes = reporteService.listarPorEmpresaDTO(nit); // ⭐ Cambio aquí
            
            EmpresaCliente empresa = empresaService.buscarPorNit(nit)
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));

            double totalGeneral = reportes.stream()
                    .mapToDouble(ReporteResponseDTO::getTotalConsumos) // ⭐ Cambio aquí
                    .sum();

            ResumenEmpresaDTO resumen = new ResumenEmpresaDTO();
            resumen.setEmpresa(empresa);
            resumen.setTotalReportes(reportes.size());
            resumen.setTotalConsumoGeneral(totalGeneral);
            resumen.setReportes(reportes); // ⭐ Ahora usa DTOs

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ⭐ ACTUALIZAR DTO PARA USAR ReporteResponseDTO
    public static class ResumenEmpresaDTO {
        private EmpresaCliente empresa;
        private int totalReportes;
        private double totalConsumoGeneral;
        private List<ReporteResponseDTO> reportes; // ⭐ Cambio de tipo

        // Getters y Setters
        public EmpresaCliente getEmpresa() { return empresa; }
        public void setEmpresa(EmpresaCliente empresa) { this.empresa = empresa; }

        public int getTotalReportes() { return totalReportes; }
        public void setTotalReportes(int totalReportes) { this.totalReportes = totalReportes; }

        public double getTotalConsumoGeneral() { return totalConsumoGeneral; }
        public void setTotalConsumoGeneral(double totalConsumoGeneral) { this.totalConsumoGeneral = totalConsumoGeneral; }

        public List<ReporteResponseDTO> getReportes() { return reportes; } // ⭐ Cambio de tipo
        public void setReportes(List<ReporteResponseDTO> reportes) { this.reportes = reportes; }
    }
}