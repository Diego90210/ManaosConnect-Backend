package com.diego.gestorcasino.services;

import com.diego.gestorcasino.models.Empleado;
import com.diego.gestorcasino.repositories.EmpleadoRepository;
import com.diego.gestorcasino.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EmpresaRepository empresaRepository; // Agrega el repositorio de Empresa

    // Obtener todos los empleados
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    // Obtener un empleado por cédula
    public Empleado obtenerEmpleadoPorCedula(Long cedula) {
        return empleadoRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
    }

    // anadir un nuevo empleado
    public Empleado anadirEmpleado(Empleado empleado) {
        // Validar si la empresa existe por NIT
        empresaRepository.findById(empleado.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + empleado.getEmpresaNIT()));

        return empleadoRepository.save(empleado);
    }

    // Actualizar un empleado
    public Empleado actualizarEmpleado(Long cedula, Empleado detallesEmpleado) {
        Empleado empleadoExistente = empleadoRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));

        empleadoExistente.setNombre(detallesEmpleado.getNombre());
        empleadoExistente.setEmpresaNIT(detallesEmpleado.getEmpresaNIT()); // Actualizar NIT de la empresa

        // Validar si la empresa existe
        empresaRepository.findById(detallesEmpleado.getEmpresaNIT())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con NIT: " + detallesEmpleado.getEmpresaNIT()));

        return empleadoRepository.save(empleadoExistente);
    }

    // Borrar un empleado
    public void eliminarEmpleado(Long cedula) {
        Empleado empleado = empleadoRepository.findById(cedula)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con cédula: " + cedula));
        empleadoRepository.delete(empleado);
    }
}


