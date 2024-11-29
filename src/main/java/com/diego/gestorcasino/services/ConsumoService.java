package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.ConsumoDTO;
import com.diego.gestorcasino.dto.PlatoConsumoDTO;
import com.diego.gestorcasino.models.Empleado;
import com.diego.gestorcasino.models.Plato;
import com.diego.gestorcasino.models.PlatoConsumo;
import com.diego.gestorcasino.models.Consumo;
import com.diego.gestorcasino.repositories.ConsumoRepository;
import com.diego.gestorcasino.repositories.EmpleadoRepository;
import com.diego.gestorcasino.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsumoService {

    @Autowired
    private ConsumoRepository consumoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    public List<ConsumoDTO> obtenerTodosLosConsumos() {
        List<Consumo> consumos = consumoRepository.findAll();

        return consumos.stream().map(consumo -> {
            Empleado empleado = empleadoRepository.findByCedula(consumo.getCedulaEmpleado())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + consumo.getCedulaEmpleado()));

            ConsumoDTO consumoDTO = new ConsumoDTO();
            consumoDTO.setId(consumo.getId());
            consumoDTO.setCedulaEmpleado(empleado.getCedula());
            consumoDTO.setNombreEmpleado(empleado.getNombre());
            consumoDTO.setRutaImagenEmpleado(empleado.getRutaImagen());
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
        }).collect(Collectors.toList());
    }

    public List<ConsumoDTO> obtenerConsumosPorEmpleado(String cedulaEmpleado) {
        // Validar que el empleado existe antes de buscar los consumos
        Empleado empleado = empleadoRepository.findByCedula(cedulaEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedulaEmpleado));

        // Obtener consumos asociados al empleado
        List<Consumo> consumos = consumoRepository.findByCedulaEmpleado(cedulaEmpleado);

        // Convertir cada consumo a ConsumoDTO
        return consumos.stream().map(consumo -> {
            ConsumoDTO consumoDTO = new ConsumoDTO();
            consumoDTO.setId(consumo.getId());
            consumoDTO.setCedulaEmpleado(empleado.getCedula());
            consumoDTO.setNombreEmpleado(empleado.getNombre());
            consumoDTO.setRutaImagenEmpleado(empleado.getRutaImagen()); // Ruta de la imagen
            consumoDTO.setFecha(consumo.getFecha().toString());
            consumoDTO.setTotal(consumo.getTotal());

            // Convertir los platos consumidos a PlatoConsumoDTO
            consumoDTO.setPlatosConsumidos(consumo.getPlatosConsumidos().stream()
                    .map(platoConsumo -> {
                        PlatoConsumoDTO platoDTO = new PlatoConsumoDTO();
                        platoDTO.setNombrePlato(platoConsumo.getNombrePlato());
                        platoDTO.setCantidad(platoConsumo.getCantidad());
                        return platoDTO;
                    })
                    .collect(Collectors.toList()));

            return consumoDTO;
        }).collect(Collectors.toList());
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



    // Eliminar un consumo
    public void eliminarConsumo(int id) {
        // Buscar el consumo
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));
        consumoRepository.delete(consumo);
    }

    public ConsumoDTO obtenerConsumoPorId(int id) {
        Consumo consumo = consumoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumo no encontrado con id: " + id));

        Empleado empleado = empleadoRepository.findByCedula(consumo.getCedulaEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + consumo.getCedulaEmpleado()));

        ConsumoDTO consumoDTO = new ConsumoDTO();
        consumoDTO.setId(consumo.getId());
        consumoDTO.setCedulaEmpleado(empleado.getCedula());
        consumoDTO.setNombreEmpleado(empleado.getNombre());
        consumoDTO.setRutaImagenEmpleado(empleado.getRutaImagen());
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

    public ConsumoDTO convertirAConsumoDTO(Consumo consumo) {
        Empleado empleado = empleadoRepository.findByCedula(consumo.getCedulaEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + consumo.getCedulaEmpleado()));

        ConsumoDTO consumoDTO = new ConsumoDTO();
        consumoDTO.setId(consumo.getId());
        consumoDTO.setCedulaEmpleado(empleado.getCedula());
        consumoDTO.setNombreEmpleado(empleado.getNombre());
        consumoDTO.setRutaImagenEmpleado("/empleados/" + empleado.getCedula() + "/imagen"); // URL de la imagen
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
