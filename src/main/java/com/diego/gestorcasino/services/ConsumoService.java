package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.models.PlatoConsumo;
import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.EmpleadoRepository;
import com.diego.gestorcasino.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    // Obtener todos los consumos
    public List<Consumo> obtenerTodosLosConsumos() {
        return consumoRepository.findAll();
    }

    // Obtener los consumos por empleado
    public List<Consumo> obtenerConsumosPorEmpleado(String cedulaEmpleado) {
        return consumoRepository.findByCedulaEmpleado(cedulaEmpleado);
    }

    // Obtener un consumo por su ID
    public Consumo obtenerConsumoPorId(int id) {
        return consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
    }

    // Añadir un nuevo consumo
    public Consumo anadirConsumo(String cedulaEmpleado, Consumo consumo) {
        // Verificar si el empleado existe
        if (empleadoRepository.findByCedula(cedulaEmpleado).isEmpty()) {
            throw new RuntimeException("Empleado no encontrado con cédula: " + cedulaEmpleado);
        }
        consumo.setCedulaEmpleado(cedulaEmpleado);

        for (PlatoConsumo platoConsumo : consumo.getPlatosConsumidos()) {
            // Buscar el plato por nombre
            Plato plato = platoRepository.findByNombreIgnoreCase(platoConsumo.getNombrePlato())
                    .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + platoConsumo.getNombrePlato()));

            // Validar el precio del plato
            if (plato.getPrecio() <= 0) {
                throw new RuntimeException("Precio inválido para el plato: " + plato.getNombre());
            }

            // Asignar el nombre del plato al PlatoConsumo
            platoConsumo.setNombrePlato(plato.getNombre());

            // Asociar el consumo al PlatoConsumo
            platoConsumo.setConsumo(consumo);
        }

        // Calcular el total basado en los platos consumidos
        double total = consumo.getPlatosConsumidos()
                .stream()
                .mapToDouble(platoConsumo -> platoRepository.findByNombreIgnoreCase(platoConsumo.getNombrePlato())
                        .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + platoConsumo.getNombrePlato()))
                        .getPrecio() * platoConsumo.getCantidad())
                .sum();
        consumo.setTotal(total);

        return consumoRepository.save(consumo);
    }

    // Actualizar un consumo
    public Consumo actualizarConsumo(int id, Consumo detallesConsumo) {
        // Buscar el consumo existente
        Consumo consumoExistente = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));

        // Actualizar la fecha
        consumoExistente.setFecha(detallesConsumo.getFecha());

        // Actualizar los platos consumidos
        consumoExistente.setPlatosConsumidos(detallesConsumo.getPlatosConsumidos());

        // Recalcular el total basado en los nombres de los platos consumidos
        double total = consumoExistente.getPlatosConsumidos()
                .stream()
                .mapToDouble(platoConsumo -> {
                    // Buscar el plato por nombre
                    Plato plato = platoRepository.findByNombreIgnoreCase(platoConsumo.getNombrePlato())
                            .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + platoConsumo.getNombrePlato()));

                    // Calcular subtotal por plato
                    return plato.getPrecio() * platoConsumo.getCantidad();
                })
                .sum();

        consumoExistente.setTotal(total);

        return consumoRepository.save(consumoExistente);
    }


    // Eliminar un consumo
    public void eliminarConsumo(int id) {
        // Buscar el consumo
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
        consumoRepository.delete(consumo);
    }
}
