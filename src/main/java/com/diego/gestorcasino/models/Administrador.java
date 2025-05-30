
package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
public class Administrador extends PersonaBase {

    @Id
    private String cedula;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Esta anotación debe estar SOLA - sin @JoinColumn
    private Usuario usuario;

    // Constructores
    public Administrador() {}

    public Administrador(Usuario usuario) {
        this.usuario = usuario;
        this.cedula = usuario.getCedula();
    }

    // Getters y Setters
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.cedula = usuario.getCedula(); // Auto-sincronización
        }
    }
}