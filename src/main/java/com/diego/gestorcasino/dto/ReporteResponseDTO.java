package com.diego.gestorcasino.dto;

import java.time.LocalDate;
import java.util.List;

public class ReporteResponseDTO {
    private int id;
    private String empresaNit;
    private String empresaNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private double totalConsumos;
    private List<EmpleadoReporteDTO> empleados; // NUEVO CAMPO

    public ReporteResponseDTO() {}

    public ReporteResponseDTO(int id, String empresaNit, String empresaNombre, 
                            LocalDate fechaInicio, LocalDate fechaFin, double totalConsumos,
                            List<EmpleadoReporteDTO> empleados) { // ⭐ NUEVO PARÁMETRO
        this.id = id;
        this.empresaNit = empresaNit;
        this.empresaNombre = empresaNombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.totalConsumos = totalConsumos;
        this.empleados = empleados; // ASIGNAR EMPLEADOS
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmpresaNit() { return empresaNit; }
    public void setEmpresaNit(String empresaNit) { this.empresaNit = empresaNit; }

    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public double getTotalConsumos() { return totalConsumos; }
    public void setTotalConsumos(double totalConsumos) { this.totalConsumos = totalConsumos; }

    public List<EmpleadoReporteDTO> getEmpleados() { return empleados; } // NUEVO GETTER
    public void setEmpleados(List<EmpleadoReporteDTO> empleados) { this.empleados = empleados; } // NUEVO SETTER
}