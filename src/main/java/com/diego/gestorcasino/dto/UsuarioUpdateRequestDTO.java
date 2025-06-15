package com.diego.gestorcasino.dto;

public class UsuarioUpdateRequestDTO {
    private String email;
    private String nombre;
    private String password;
    private String telefono;

    public UsuarioUpdateRequestDTO(String email, String nombre, String telefono, String password) {
        this.email = email;
        this.nombre = nombre;
        this.telefono = telefono;
        this.password = password;
    }


    // Getters y setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}


