package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Empleado;
import com.diego.gestorcasino.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    // Obtener todos los empleados
    @GetMapping
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoService.obtenerTodosLosEmpleados();
    }

    // Obtener un empleado por cédula
    @GetMapping("/{cedula}")
    public ResponseEntity<Empleado> obtenerEmpleadoPorCedula(@PathVariable String cedula) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorCedula(cedula);
        return ResponseEntity.ok(empleado);
    }

    // anadir un nuevo empleado
    @PostMapping
    public ResponseEntity<Empleado> anadirEmpleado(@RequestBody Empleado empleado) {
        Empleado nuevoEmpleado = empleadoService.anadirEmpleado(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    // Actualizar un empleado existente
    @PutMapping("/{cedula}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable String cedula, @RequestBody Empleado detallesEmpleado) {
        Empleado empleadoActualizado = empleadoService.actualizarEmpleado(cedula, detallesEmpleado);
        return ResponseEntity.ok(empleadoActualizado);
    }

    // Eliminar un empleado
    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable String cedula) {
        empleadoService.eliminarEmpleado(cedula);
        return ResponseEntity.noContent().build();
    }
}


