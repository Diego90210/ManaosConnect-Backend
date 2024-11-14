package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    private Long cedula;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Long empresaNIT;

    @Column(nullable = false)
    private Long telefono;

    // Getters y Setters

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public Long getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getEmpresaNIT() {
        return empresaNIT;
    }

    public void setCedula(Long cedula) {
        this.cedula = cedula;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmpresaNIT(Long empresaNIT) {
        this.empresaNIT = empresaNIT;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "cedula=" + cedula +
                ", nombre='" + nombre + '\'' +
                ", empresaNIT=" + empresaNIT +
                ", telefono=" + telefono +
                '}';
    }
}

