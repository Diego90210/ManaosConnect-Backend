package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.ConsumoDTO;
import com.diego.gestorcasino.dto.PlatoConsumoDTO;
import com.diego.gestorcasino.models.Consumidor;
import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.models.PlatoConsumo;
import com.diego.gestorcasino.repositories.ConsumidorRepository;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private ConsumidorRepository consumidorRepository;

    @Autowired
    private PlatoRepository platoRepository;

    // MÉTODOS ESTANDARIZADOS (nombres consistentes)
    public Consumo guardar(Consumo consumo) {
        // Verificar si el empleado existe
        if (consumidorRepository.findByCedula(consumo.getCedulaEmpleado()).isEmpty()) {
            throw new RuntimeException("Consumidor no encontrado con cédula: " + consumo.getCedulaEmpleado());
        }

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

    public Consumo actualizar(int id, Consumo detallesConsumo) {
        // Buscar el consumo existente
        Consumo consumoExistente = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));

        // Actualizar la fecha del consumo
        consumoExistente.setFecha(detallesConsumo.getFecha());

        // Combinar platos existentes con los nuevos
        List<PlatoConsumo> platosExistentes = consumoExistente.getPlatosConsumidos();
        List<PlatoConsumo> platosNuevos = detallesConsumo.getPlatosConsumidos();

        for (PlatoConsumo platoNuevo : platosNuevos) {
            // Buscar el plato por nombre en los platos existentes
            PlatoConsumo platoExistente = platosExistentes.stream()
                    .filter(p -> p.getNombrePlato().equalsIgnoreCase(platoNuevo.getNombrePlato()))
                    .findFirst()
                    .orElse(null);

            if (platoExistente != null) {
                // Si el plato ya existe, actualizar la cantidad
                platoExistente.setCantidad(platoExistente.getCantidad() + platoNuevo.getCantidad());
            } else {
                // Si es un nuevo plato, verificar su existencia en la base de datos
                Plato plato = platoRepository.findByNombreIgnoreCase(platoNuevo.getNombrePlato())
                        .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + platoNuevo.getNombrePlato()));

                // Crear una nueva relación entre el consumo y el plato
                platoNuevo.setConsumo(consumoExistente);
                platoNuevo.setNombrePlato(plato.getNombre());
                platosExistentes.add(platoNuevo);
            }
        }

        // Actualizar la lista de platos consumidos en el consumo existente
        consumoExistente.setPlatosConsumidos(platosExistentes);

        // Recalcular el total basado en los platos consumidos
        double total = platosExistentes.stream()
                .mapToDouble(platoConsumo -> {
                    Plato plato = platoRepository.findByNombreIgnoreCase(platoConsumo.getNombrePlato())
                            .orElseThrow(() -> new RuntimeException("Plato no encontrado con nombre: " + platoConsumo.getNombrePlato()));

                    return plato.getPrecio() * platoConsumo.getCantidad();
                })
                .sum();

        consumoExistente.setTotal(total);

        return consumoRepository.save(consumoExistente);
    }

    public void eliminar(int id) {
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
        consumoRepository.delete(consumo);
    }

    public List<Consumo> listarTodos() {
        return consumoRepository.findAll();
    }

    public Optional<Consumo> buscarPorId(int id) {
        return consumoRepository.findById(id);
    }

    // MÉTODOS ESPECÍFICOS PARA REPORTES
    public List<Consumo> listarPorEmpresa(String nit) {
        return consumoRepository.findAll().stream()
                .filter(consumo -> {
                    Optional<Consumidor> consumidor = consumidorRepository.findByCedula(consumo.getCedulaEmpleado());
                    return consumidor.isPresent() && nit.equals(consumidor.get().getEmpresaNIT());
                })
                .toList();
    }

    public List<Consumo> listarPorEmpresaEnPeriodo(String nit, LocalDate fechaInicio, LocalDate fechaFin) {
        return consumoRepository.findAll().stream()
                .filter(consumo -> {
                    Optional<Consumidor> consumidor = consumidorRepository.findByCedula(consumo.getCedulaEmpleado());
                    return consumidor.isPresent() && nit.equals(consumidor.get().getEmpresaNIT());
                })
                .filter(consumo -> !consumo.getFecha().isBefore(fechaInicio) && !consumo.getFecha().isAfter(fechaFin))
                .toList();
    }

    public double calcularTotalConsumosPorEmpresa(String nit) {
        return listarPorEmpresa(nit).stream()
                .mapToDouble(Consumo::getTotal)
                .sum();
    }

    public double calcularTotalConsumosPorEmpresaEnPeriodo(String nit, LocalDate fechaInicio, LocalDate fechaFin) {
        return listarPorEmpresaEnPeriodo(nit, fechaInicio, fechaFin).stream()
                .mapToDouble(Consumo::getTotal)
                .sum();
    }

    // MÉTODOS EXISTENTES (mantener compatibilidad)
    public List<ConsumoDTO> obtenerTodosLosConsumos() {
        List<Consumo> consumos = consumoRepository.findAll();
        return consumos.stream().map(this::convertirAConsumoDTO).collect(Collectors.toList());
    }

    public List<ConsumoDTO> obtenerConsumosPorEmpleado(String cedulaEmpleado) {
        // Validar que el empleado existe antes de buscar los consumos
        Consumidor consumidor = consumidorRepository.findByCedula(cedulaEmpleado)
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + cedulaEmpleado));

        // Obtener consumos asociados al empleado
        List<Consumo> consumos = consumoRepository.findByCedulaEmpleado(cedulaEmpleado);

        // Convertir cada consumo a ConsumoDTO
        return consumos.stream().map(this::convertirAConsumoDTO).collect(Collectors.toList());
    }

    public Consumo anadirConsumo(String cedulaEmpleado, Consumo consumo) {
        consumo.setCedulaEmpleado(cedulaEmpleado);
        return guardar(consumo);
    }

    public Consumo actualizarConsumo(int id, Consumo detallesConsumo) {
        return actualizar(id, detallesConsumo);
    }

    public void eliminarConsumo(int id) {
        eliminar(id);
    }

    public ConsumoDTO obtenerConsumoPorId(int id) {
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
        return convertirAConsumoDTO(consumo);
    }

    public ConsumoDTO convertirAConsumoDTO(Consumo consumo) {
        Consumidor consumidor = consumidorRepository.findByCedula(consumo.getCedulaEmpleado())
                .orElseThrow(() -> new RuntimeException("Consumidor no encontrado con cédula: " + consumo.getCedulaEmpleado()));

        ConsumoDTO consumoDTO = new ConsumoDTO();
        consumoDTO.setId(consumo.getId());
        consumoDTO.setCedulaEmpleado(consumidor.getCedula());
        consumoDTO.setNombreEmpleado(consumidor.getNombre());
        consumoDTO.setRutaImagenEmpleado(consumidor.getRutaImagen());
        consumoDTO.setFecha(consumo.getFecha().toString());
        consumoDTO.setTotal(consumo.getTotal());
        consumoDTO.setPlatosConsumidos(consumo.getPlatosConsumidos().stream()
                .map(platoConsumo -> {
                    PlatoConsumoDTO platoDTO = new PlatoConsumoDTO();
                    platoDTO.setNombrePlato(platoConsumo.getNombrePlato());
                    platoDTO.setCantidad(platoConsumo.getCantidad());
                    return platoDTO;
                })
                .collect(Collectors.toList()));

        return consumoDTO;
    }
}