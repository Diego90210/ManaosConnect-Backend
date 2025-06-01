package com.diego.gestorcasino.dto;

public class AdministradorResponseDTO {
    private String cedula;
    private String nombre;
    private String telefono;
    private String usuarioEmail; // Solo datos básicos del usuario

    // Constructores
    public AdministradorResponseDTO(String cedula, String nombre, String telefono, String usuarioEmail) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.usuarioEmail = usuarioEmail;
    }

    // Getters y setters
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
}
