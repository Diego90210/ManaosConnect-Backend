package com.diego.gestorcasino.dto;

import java.util.List;

public class ConsumoDTO {

    private int id;
    private String cedulaConsumidor;
    private String fecha;
    private double total;
    private String nombreConsumidor;
    private String rutaImagenConsumidor;
    private List<PlatoConsumoDTO> platosConsumidos;

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedulaConsumidor() {
        return cedulaConsumidor;
    }

    public void setCedulaConsumidor(String cedulaConsumidor) {
        this.cedulaConsumidor = cedulaConsumidor;
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

    public String getNombreConsumidor() {
        return nombreConsumidor;
    }

    public void setNombreConsumidor(String nombreConsumidor) {
        this.nombreConsumidor = nombreConsumidor;
    }

    public String getRutaImagenConsumidor() {
        return rutaImagenConsumidor;
    }

    public void setRutaImagenConsumidor(String rutaImagenConsumidor) {
        this.rutaImagenConsumidor = rutaImagenConsumidor;
    }

}