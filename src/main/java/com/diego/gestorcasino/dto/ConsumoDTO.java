package com.diego.gestorcasino.dto;

import java.util.List;

public class ConsumoDTO { // Cambiar a public
    private int id;
    private String cedulaEmpleado;
    private String nombreEmpleado;
    private String rutaImagenEmpleado;
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

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getRutaImagenEmpleado() {
        return rutaImagenEmpleado;
    }

    public void setRutaImagenEmpleado(String rutaImagenEmpleado) {
        this.rutaImagenEmpleado = rutaImagenEmpleado;
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

