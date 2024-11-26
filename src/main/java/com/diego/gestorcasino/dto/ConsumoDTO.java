package com.diego.gestorcasino.dto;

import java.util.List;

public class ConsumoDTO {

    private int id;
    private String cedulaEmpleado;
    private String fecha;
    private double total;
    private List<PlatoConsumoDTO> platosConsumidos;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    public void setCedulaEmpleado(String cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<PlatoConsumoDTO> getPlatosConsumidos() {
        return platosConsumidos;
    }

    public void setPlatosConsumidos(List<PlatoConsumoDTO> platosConsumidos) {
        this.platosConsumidos = platosConsumidos;
    }
}

class PlatoConsumoDTO {
    private String nombrePlato;
    private int cantidad;

    // Getters y Setters
    public String getNombrePlato() {
        return nombrePlato;
    }

    public void setNombrePlato(String nombrePlato) {
        this.nombrePlato = nombrePlato;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
