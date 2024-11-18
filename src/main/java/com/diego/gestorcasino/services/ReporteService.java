package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.models.Empresa;
import com.diego.gestorcasino.models.Reporte;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.EmpresaRepository;
import com.diego.gestorcasino.repositories.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ConsumoRepository consumoRepository;

    // Obtener todos los reportes
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    // Obtener reportes por empresa (por NIT)
    public List<Reporte> obtenerReportesPorEmpresa(Long nit) {
        return reporteRepository.findByEmpresaNit(nit);
    }

    // Obtener un reporte por su ID
    public Reporte obtenerReportePorId(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    // Crear un nuevo reporte basado en un intervalo de fechas y una empresa
    public Reporte crearReporte(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
        Empresa empresa = empresaRepository.findByNit(nitEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nitEmpresa));

        List<Consumo> consumos = consumoRepository.findAll().stream()
                .filter(consumo -> consumo.getCedulaEmpleado() != null)  // Aquí puedes agregar una validación si es necesario
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());

        double totalConsumos = consumos.stream().mapToDouble(Consumo::getTotal).sum();

        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setEmpresa(empresa);
        nuevoReporte.setFechaInicio(fechaInicio);
        nuevoReporte.setFechaFin(fechaFin);
        nuevoReporte.setTotalConsumos(totalConsumos);

        return reporteRepository.save(nuevoReporte);
    }

    // Eliminar un reporte
    public void eliminarReporte(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        reporteRepository.delete(reporte);
    }
}
