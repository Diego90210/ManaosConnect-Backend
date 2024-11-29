package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Empleado;
import com.diego.gestorcasino.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity<Empleado> anadirEmpleado(
            @RequestPart("empleado") Empleado empleado,
            @RequestPart("imagen") MultipartFile imagen) {
        try {
            Empleado nuevoEmpleado = empleadoService.anadirEmpleadoConImagen(empleado, imagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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

    @PostMapping("/{cedula}/subirImagen")
    public ResponseEntity<String> subirImagen(@PathVariable String cedula, @RequestParam("imagen") MultipartFile archivo) {
        try {
            empleadoService.guardarImagen(cedula, archivo);
            return ResponseEntity.ok("Imagen subida exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al subir la imagen: " + e.getMessage());
        }
    }

    @DeleteMapping("/{cedula}/eliminarImagen")
    public ResponseEntity<String> eliminarImagen(@PathVariable String cedula) {
        try {
            empleadoService.eliminarImagen(cedula);
            return ResponseEntity.ok("Imagen eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{cedula}/modificarImagen")
    public ResponseEntity<String> modificarImagen(@PathVariable String cedula, @RequestParam("imagen") MultipartFile nuevaImagen) {
        try {
            empleadoService.modificarImagen(cedula, nuevaImagen);
            return ResponseEntity.ok("Imagen actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen");
        }
    }

    @GetMapping("/{cedula}/imagen")
    public ResponseEntity<byte[]> obtenerImagenEmpleado(@PathVariable String cedula) {
        Empleado empleado = empleadoService.obtenerEmpleadoPorCedula(cedula);

        if (empleado.getRutaImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path rutaImagen = Paths.get(empleado.getRutaImagen());
            byte[] imagen = Files.readAllBytes(rutaImagen);

            // Determinar el tipo de contenido (por ejemplo, image/jpeg)
            String contentType = Files.probeContentType(rutaImagen);

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(imagen);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}


