package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.models.Reporte;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
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
    private EmpresaClienteRepository empresaClienteRepository;

    @Autowired
    private ConsumoRepository consumoRepository;

    // Obtener todos los reportes
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    // Obtener reportes por empresa (por NIT)
    public List<Reporte> obtenerReportesPorEmpresa(String nit) {
        return reporteRepository.findByEmpresaCliente_Nit(nit);
    }

    // Obtener un reporte por su ID
    public Reporte obtenerReportePorId(int id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    // Crear un nuevo reporte basado en un intervalo de fechas y una empresa
    public Reporte crearReporte(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
        // Verificar si la empresa existe
        com.diego.gestorcasino.models.EmpresaCliente empresaCliente = this.empresaClienteRepository.findByNit(nitEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nitEmpresa));

        // Obtener los consumos dentro del rango de fechas
        List<Consumo> consumos = consumoRepository.findAll().stream()
                .filter(consumo -> consumo.getCedulaEmpleado() != null)
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Calcular el total de los consumos
        double totalConsumos = consumos.stream()
                .mapToDouble(Consumo::getTotal)
                .sum();

        // Crear y guardar el reporte
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setEmpresaCliente(empresaCliente);
        nuevoReporte.setFechaInicio(fechaInicio);
        nuevoReporte.setFechaFin(fechaFin);
        nuevoReporte.setTotalConsumos(totalConsumos);

        return reporteRepository.save(nuevoReporte);
    }

    // Eliminar un reporte
    public void eliminarReporte(int id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        reporteRepository.delete(reporte);
    }
}
