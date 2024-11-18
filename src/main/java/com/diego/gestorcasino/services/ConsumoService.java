package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Plato;
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

        // Calcular el total en base a los platos consumidos
        double total = consumo.getPlatosConsumidos()
                .stream()
                .mapToDouble(Plato::getPrecio)
                .sum();
        consumo.setTotal(total);

        return consumoRepository.save(consumo);
    }

    // Actualizar un consumo
    public Consumo actualizarConsumo(int id, Consumo detallesConsumo) {
        Consumo consumoExistente = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));

        consumoExistente.setFecha(detallesConsumo.getFecha());
        consumoExistente.setPlatosConsumidos(detallesConsumo.getPlatosConsumidos());

        double total = consumoExistente.getPlatosConsumidos()
                .stream()
                .mapToDouble(Plato::getPrecio)
                .sum();
        consumoExistente.setTotal(total);

        return consumoRepository.save(consumoExistente);
    }

    // Eliminar un consumo
    public void eliminarConsumo(int id) {
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
        consumoRepository.delete(consumo);
    }
}
