package com.diego.gestorcasino.dto;

public class ConsumidorResponseDTO {
    private String cedula;
    private String nombre;
    private String telefono;
    private String empresaNIT;
    private String imagenUrl; // opcional

    public ConsumidorResponseDTO(String cedula, String nombre, String telefono, String empresaNIT, String imagenUrl) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.empresaNIT = empresaNIT;
        this.imagenUrl = imagenUrl;
    }

    // Getters y setters
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getEmpresaNIT() { return empresaNIT; }
    public String getImagenUrl() { return imagenUrl; }
}

