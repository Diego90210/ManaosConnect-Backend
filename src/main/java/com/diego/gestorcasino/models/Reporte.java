package com.diego.gestorcasino.models;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_nit", nullable = false)
    private EmpresaCliente empresaCliente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "total_consumos", nullable = false)
    private double totalConsumos;

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmpresaCliente getEmpresaCliente() {
        return empresaCliente;
    }

    public void setEmpresaCliente(EmpresaCliente empresaCliente) {
        this.empresaCliente = empresaCliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getTotalConsumos() {
        return totalConsumos;
    }

    public void setTotalConsumos(double totalConsumos) {
        this.totalConsumos = totalConsumos;
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", empresa=" + empresaCliente +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", totalConsumos=" + totalConsumos +
                '}';
    }
}

