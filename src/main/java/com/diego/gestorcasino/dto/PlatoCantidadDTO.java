package com.diego.gestorcasino.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlatoCantidadDTO {

    private String nombrePlato;
    private int cantidad;

    @JsonIgnore
    private double precioUnitario;

    // Getters y setters
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

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}

