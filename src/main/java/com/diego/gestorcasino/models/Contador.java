package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contadores")
public class Contador extends PersonaBase {

    @Id
    private String cedula;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Usuario usuario;

    public Contador() {}

    public Contador(Usuario usuario) {
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

