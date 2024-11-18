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
    private int id;

    @Column(nullable = false)
    private String cedulaEmpleado;  // Solo se almacena la cédula del empleado

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Plato> getPlatosConsumidos() {
        return platosConsumidos;
    }

    public void setPlatosConsumidos(List<Plato> platosConsumidos) {
        this.platosConsumidos = platosConsumidos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getCedulaEmpleado() {
        return cedulaEmpleado;
    }

    public void setCedulaEmpleado(String cedulaEmpleado) {
        this.cedulaEmpleado = cedulaEmpleado;
    }

    @Override
    public String toString() {
        return "Consumo{" +
                "id=" + id +
                ", cedulaEmpleado='" + cedulaEmpleado + '\'' +
                ", fecha=" + fecha +
                ", total=" + total +
                ", platosConsumidos=" + platosConsumidos +
                '}';
    }
}
