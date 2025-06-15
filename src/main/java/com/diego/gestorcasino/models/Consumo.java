package com.diego.gestorcasino.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "consumos")
public class Consumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String cedulaCajero;

    @Column(nullable = false)
    private String cedulaConsumidor;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private double total;

    @OneToMany(mappedBy = "consumo", cascade = CascadeType.ALL)
    private List<PlatoConsumo> platosConsumidos;


    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCedulaConsumidor() {
        return cedulaConsumidor;
    }

    public void setCedulaConsumidor(String cedulaEmpleado) {
        this.cedulaConsumidor = cedulaEmpleado;
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

    public List<PlatoConsumo> getPlatosConsumidos() {
        return platosConsumidos;
    }

    public String getCedulaCajero() {
        return cedulaCajero;
    }

    public void setCedulaCajero(String cedulaCajero) {
        this.cedulaCajero = cedulaCajero;
    }

    public void setPlatosConsumidos(List<PlatoConsumo> platosConsumidos) {
        this.platosConsumidos = platosConsumidos;
    }
}
