package com.diego.gestorcasino.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consumos")
public class Consumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cedulaEmpleado;  // Solo se almacena la cédula del empleado

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private double total;

    @ManyToMany
    @JoinTable(
            name = "consumo_platos",
            joinColumns = @JoinColumn(name = "consumo_id"),
            inverseJoinColumns = @JoinColumn(name = "plato_id")
    )
    private List<Plato> platosConsumidos = new ArrayList<>();

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    public void setCedulaEmpleado(Long cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<Plato> getPlatosConsumidos() {
        return platosConsumidos;
    }

    public void setPlatosConsumidos(List<Plato> platosConsumidos) {
        this.platosConsumidos = platosConsumidos;
    }
}
