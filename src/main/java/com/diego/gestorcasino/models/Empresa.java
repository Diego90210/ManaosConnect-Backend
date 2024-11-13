package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "empresas")
public class Empresa {
    @Id
    private Long nit;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Long telefono;

    @Column(nullable = false)
    private String contacto; // Nuevo atributo

    // Getters y Setters

    public Long getNit() {
        return nit;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public Long getTelefono() {
        return telefono;
    }

    public String getContacto() { // Getter para contacto
        return contacto;
    }

    public void setNit(Long nit) {
        this.nit = nit;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public void setContacto(String contacto) { // Setter para contacto
        this.contacto = contacto;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "nit=" + nit +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono=" + telefono +
                ", contacto='" + contacto + '\'' + // Añadido al toString
                '}';
    }
}

