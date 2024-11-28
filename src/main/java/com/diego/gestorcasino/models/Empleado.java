package com.diego.gestorcasino.models;

import jakarta.persistence.*;

@Entity
@Table(name = "empleados")
public class Empleado {
    @Id
    private String cedula;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String empresaNIT;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = true) // Ruta de la imagen, puede ser nula inicialmente
    private String rutaImagen;

    public String getCedula() {
        return cedula;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmpresaNIT(String empresaNIT) {
        this.empresaNIT = empresaNIT;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmpresaNIT() {
        return empresaNIT;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "cedula='" + cedula + '\'' +
                ", nombre='" + nombre + '\'' +
                ", empresaNIT='" + empresaNIT + '\'' +
                ", telefono='" + telefono + '\'' +
                ", rutaImagen='" + rutaImagen + '\'' +
                '}';
    }
}

