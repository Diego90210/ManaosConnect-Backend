package com.diego.gestorcasino.dto;

public class PlatoConsumoDTO {
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
