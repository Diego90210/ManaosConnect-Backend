package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cajeros")
public class Cajero extends PersonaBase {

    @Id
    private String cedula;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Usuario usuario;

    public Cajero() {}

    public Cajero(Usuario usuario) {
        this.usuario = usuario;
        this.cedula = usuario.getCedula();
    }

    // Getters y setters con auto-sincronización
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.cedula = usuario.getCedula();
        }
    }
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Usuario getUsuario() {
        return usuario;
    }

}

