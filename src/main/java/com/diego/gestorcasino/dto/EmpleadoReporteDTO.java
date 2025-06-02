package com.diego.gestorcasino.dto;

public class EmpleadoReporteDTO {
    private String cedula;
    private String nombre;
    private double totalConsumido;
    private int cantidadConsumos;

    public EmpleadoReporteDTO() {}

    public EmpleadoReporteDTO(String cedula, String nombre, double totalConsumido, int cantidadConsumos) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.totalConsumido = totalConsumido;
        this.cantidadConsumos = cantidadConsumos;
    }

    // Getters y Setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getTotalConsumido() { return totalConsumido; }
    public void setTotalConsumido(double totalConsumido) { this.totalConsumido = totalConsumido; }

    public int getCantidadConsumos() { return cantidadConsumos; }
    public void setCantidadConsumos(int cantidadConsumos) { this.cantidadConsumos = cantidadConsumos; }
}