package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.EmpleadoReporteDTO;
import com.diego.gestorcasino.dto.ReporteRequestDTO;
import com.diego.gestorcasino.dto.ReporteResponseDTO;
import com.diego.gestorcasino.models.Consumidor;
import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.models.EmpresaCliente;
import com.diego.gestorcasino.models.Reporte;
import com.diego.gestorcasino.repositories.ConsumidorRepository;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.EmpresaClienteRepository;
import com.diego.gestorcasino.repositories.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private EmpresaClienteRepository empresaClienteRepository;

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private ConsumidorRepository consumidorRepository;

    // MÉTODOS ESTANDARIZADOS (nombres consistentes)
    public Reporte crear(ReporteRequestDTO requestDTO) {
        return crearReporte(requestDTO.getNitEmpresa(), requestDTO.getFechaInicio(), requestDTO.getFechaFin());
    }

    public void eliminar(int id) {
        eliminarReporte(id);
    }

    public List<Reporte> listarTodos() {
        return obtenerTodosLosReportes();
    }

    public Optional<Reporte> buscarPorId(int id) {
        return reporteRepository.findById(id);
    }

    public List<Reporte> listarPorEmpresa(String nit) {
        return obtenerReportesPorEmpresa(nit);
    }

    // LÓGICA PRINCIPAL DE REPORTES DE CONSUMO POR EMPRESA
    public Reporte crearReporte(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
        // Verificar si la empresa existe
        EmpresaCliente empresaCliente = empresaClienteRepository.findByNit(nitEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + nitEmpresa));

        // Obtener todos los consumidores de la empresa
        List<Consumidor> consumidoresEmpresa = consumidorRepository.findAll().stream()
                .filter(consumidor -> nitEmpresa.equals(consumidor.getEmpresaNIT()))
                .collect(Collectors.toList());

        if (consumidoresEmpresa.isEmpty()) {
            throw new RuntimeException("No hay consumidores registrados para la empresa con NIT: " + nitEmpresa);
        }

        // Obtener las cédulas de los consumidores de la empresa
        List<String> cedulasConsumidores = consumidoresEmpresa.stream()
                .map(Consumidor::getCedula)
                .collect(Collectors.toList());

        // Obtener consumos de los empleados de la empresa dentro del rango de fechas
        List<Consumo> consumosEmpresa = consumoRepository.findAll().stream()
                .filter(consumo -> consumo.getCedulaEmpleado() != null)
                .filter(consumo -> cedulasConsumidores.contains(consumo.getCedulaEmpleado()))
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Calcular el total de los consumos de la empresa
        double totalConsumos = consumosEmpresa.stream()
                .mapToDouble(Consumo::getTotal)
                .sum();

        // Verificar si ya existe un reporte para los mismos parámetros
        List<Reporte> reportesExistentes = reporteRepository.findByEmpresaCliente_Nit(nitEmpresa).stream()
                .filter(reporte -> reporte.getFechaInicio().equals(fechaInicio) && reporte.getFechaFin().equals(fechaFin))
                .collect(Collectors.toList());

        if (!reportesExistentes.isEmpty()) {
            throw new RuntimeException("Ya existe un reporte para la empresa " + nitEmpresa + 
                                     " en el período " + fechaInicio + " - " + fechaFin);
        }

        // Crear y guardar el reporte
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setEmpresaCliente(empresaCliente);
        nuevoReporte.setFechaInicio(fechaInicio);
        nuevoReporte.setFechaFin(fechaFin);
        nuevoReporte.setTotalConsumos(totalConsumos);

        return reporteRepository.save(nuevoReporte);
    }

    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    public List<Reporte> obtenerReportesPorEmpresa(String nit) {
        return reporteRepository.findByEmpresaCliente_Nit(nit);
    }

    public Reporte obtenerReportePorId(int id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    }

    public void eliminarReporte(int id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
        reporteRepository.delete(reporte);
    }

    // MÉTODOS DE ANÁLISIS Y ESTADÍSTICAS
    public double calcularTotalConsumosPorEmpresaEnPeriodo(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener todos los consumidores de la empresa
        List<Consumidor> consumidoresEmpresa = consumidorRepository.findAll().stream()
                .filter(consumidor -> nitEmpresa.equals(consumidor.getEmpresaNIT()))
                .collect(Collectors.toList());

        if (consumidoresEmpresa.isEmpty()) {
            return 0.0;
        }

        // Obtener las cédulas de los consumidores de la empresa
        List<String> cedulasConsumidores = consumidoresEmpresa.stream()
                .map(Consumidor::getCedula)
                .collect(Collectors.toList());

        // Calcular total de consumos en el período
        return consumoRepository.findAll().stream()
                .filter(consumo -> consumo.getCedulaEmpleado() != null)
                .filter(consumo -> cedulasConsumidores.contains(consumo.getCedulaEmpleado()))
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .mapToDouble(Consumo::getTotal)
                .sum();
    }

    public List<Consumo> obtenerConsumosEmpresaEnPeriodo(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
        // Obtener todos los consumidores de la empresa
        List<Consumidor> consumidoresEmpresa = consumidorRepository.findAll().stream()
                .filter(consumidor -> nitEmpresa.equals(consumidor.getEmpresaNIT()))
                .collect(Collectors.toList());

        if (consumidoresEmpresa.isEmpty()) {
            return List.of();
        }

        // Obtener las cédulas de los consumidores de la empresa
        List<String> cedulasConsumidores = consumidoresEmpresa.stream()
                .map(Consumidor::getCedula)
                .collect(Collectors.toList());

        // Obtener consumos en el período
        return consumoRepository.findAll().stream()
                .filter(consumo -> consumo.getCedulaEmpleado() != null)
                .filter(consumo -> cedulasConsumidores.contains(consumo.getCedulaEmpleado()))
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());
    }

// Añadir estos métodos al final de ReporteService:

public List<ReporteResponseDTO> listarTodosDTO() {
    return reporteRepository.findAll().stream()
        .map(this::convertirADTO)
        .toList();
}

public ReporteResponseDTO obtenerPorIdDTO(int id) {
    Reporte reporte = reporteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reporte no encontrado con id: " + id));
    return convertirADTO(reporte);
}

public List<ReporteResponseDTO> listarPorEmpresaDTO(String nit) {
    return reporteRepository.findByEmpresaCliente_Nit(nit).stream()  //
        .map(this::convertirADTO)
        .toList();
}


public ReporteResponseDTO convertirADTO(Reporte reporte) {
    // Obtener empleados que consumieron en el período del reporte
    List<EmpleadoReporteDTO> empleados = obtenerEmpleadosConConsumos(
        reporte.getEmpresaCliente().getNit(),
        reporte.getFechaInicio(),
        reporte.getFechaFin()
    );

    return new ReporteResponseDTO(
        reporte.getId(),
        reporte.getEmpresaCliente().getNit(),
        reporte.getEmpresaCliente().getNombre(),
        reporte.getFechaInicio(),
        reporte.getFechaFin(),
        reporte.getTotalConsumos(),
        empleados
    );
}

private List<EmpleadoReporteDTO> obtenerEmpleadosConConsumos(String nitEmpresa, LocalDate fechaInicio, LocalDate fechaFin) {
    // Obtener todos los consumidores de la empresa
    List<Consumidor> consumidoresEmpresa = consumidorRepository.findAll().stream()
            .filter(consumidor -> nitEmpresa.equals(consumidor.getEmpresaNIT()))
            .collect(Collectors.toList());

    if (consumidoresEmpresa.isEmpty()) {
        return List.of();
    }

    // Obtener las cédulas de los consumidores de la empresa
    List<String> cedulasConsumidores = consumidoresEmpresa.stream()
            .map(Consumidor::getCedula)
            .collect(Collectors.toList());

    // Obtener consumos en el período
    List<Consumo> consumosEmpresa = consumoRepository.findAll().stream()
            .filter(consumo -> consumo.getCedulaEmpleado() != null)
            .filter(consumo -> cedulasConsumidores.contains(consumo.getCedulaEmpleado()))
            .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
            .collect(Collectors.toList());

    // Agrupar consumos por empleado y calcular totales
    return consumosEmpresa.stream()
            .collect(Collectors.groupingBy(Consumo::getCedulaEmpleado))
            .entrySet().stream()
            .map(entry -> {
                String cedula = entry.getKey();
                List<Consumo> consumosEmpleado = entry.getValue();
                
                // Buscar información del empleado
                Consumidor consumidor = consumidorRepository.findByCedula(cedula)
                        .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedula));

                // Calcular estadísticas del empleado
                double totalConsumido = consumosEmpleado.stream()
                        .mapToDouble(Consumo::getTotal)
                        .sum();
                
                int cantidadConsumos = consumosEmpleado.size();

                return new EmpleadoReporteDTO(
                    cedula,
                    consumidor.getNombre(),
                    totalConsumido,
                    cantidadConsumos
                );
            })
            .sorted((e1, e2) -> Double.compare(e2.getTotalConsumido(), e1.getTotalConsumido())) // Ordenar por total descendente
            .collect(Collectors.toList());
}
}