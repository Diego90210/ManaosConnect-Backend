package com.diego.gestorcasino.models;


import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_nit", nullable = false)
    private Empresa empresa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "total_consumos", nullable = false)
    private double totalConsumos;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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
                ", empresa=" + empresa +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", totalConsumos=" + totalConsumos +
                '}';
    }
}

