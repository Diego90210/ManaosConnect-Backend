package com.diego.gestorcasino.dto;


import java.time.LocalDate;
import java.util.List;

public class RegistroConsumoRequest {

    private String cedulaConsumidor;
    private String cedulaCajero;
    private LocalDate fecha;
    private List<PlatoCantidadDTO> platos; // Detalles de los platos consumidos

    // Getters y setters
    public String getCedulaConsumidor() {
        return cedulaConsumidor;
    }

    public void setCedulaConsumidor(String cedulaConsumidor) {
        this.cedulaConsumidor = cedulaConsumidor;
    }

    public String getCedulaCajero() {
        return cedulaCajero;
    }

    public void setCedulaCajero(String cedulaCajero) {
        this.cedulaCajero = cedulaCajero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<PlatoCantidadDTO> getPlatos() {
        return platos;
    }

    public void setPlatos(List<PlatoCantidadDTO> platos) {
        this.platos = platos;
    }
}


