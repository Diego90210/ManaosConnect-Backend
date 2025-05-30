package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Consumidor;
import com.diego.gestorcasino.services.ConsumidorService;
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
@RequestMapping("/consumidores")
public class ConsumidorController {

    @Autowired
    private ConsumidorService consumidorService;

    // Obtener todos los consumidores
    @GetMapping
    public List<Consumidor> obtenerTodosLosConsumidores() {
        return consumidorService.obtenerTodosLosConsumidores();
    }

    // Obtener un consumidor por cédula
    @GetMapping("/{cedula}")
    public ResponseEntity<Consumidor> obtenerConsumidorPorCedula(@PathVariable String cedula) {
        Consumidor consumidor = consumidorService.obtenerConsumidorPorCedula(cedula);
        return ResponseEntity.ok(consumidor);
    }

    // anadir un nuevo consumidor
    @PostMapping
    public ResponseEntity<Consumidor> anadirConsumidor(
            @RequestPart("consumidor") Consumidor consumidor,
            @RequestPart("imagen") MultipartFile imagen) {
        try {
            Consumidor nuevoConsumidor = consumidorService.anadirConsumidorConImagen(consumidor, imagen);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoConsumidor);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Actualizar un consumidor existente
    @PutMapping("/{cedula}")
    public ResponseEntity<Consumidor> actualizarConsumidor(@PathVariable String cedula, @RequestBody Consumidor detallesConsumidor) {
        Consumidor consumidorActualizado = consumidorService.actualizarConsumidor(cedula, detallesConsumidor);
        return ResponseEntity.ok(consumidorActualizado);
    }

    // Eliminar un consumidor
    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminarConsumidor(@PathVariable String cedula) {
        consumidorService.eliminarConsumidor(cedula);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cedula}/subirImagen")
    public ResponseEntity<String> subirImagen(@PathVariable String cedula, @RequestParam("imagen") MultipartFile archivo) {
        try {
            consumidorService.guardarImagen(cedula, archivo);
            return ResponseEntity.ok("Imagen subida exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al subir la imagen: " + e.getMessage());
        }
    }

    @DeleteMapping("/{cedula}/eliminarImagen")
    public ResponseEntity<String> eliminarImagen(@PathVariable String cedula) {
        try {
            consumidorService.eliminarImagen(cedula);
            return ResponseEntity.ok("Imagen eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{cedula}/modificarImagen")
    public ResponseEntity<String> modificarImagen(@PathVariable String cedula, @RequestParam("imagen") MultipartFile nuevaImagen) {
        try {
            consumidorService.modificarImagen(cedula, nuevaImagen);
            return ResponseEntity.ok("Imagen actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen");
        }
    }

    @GetMapping("/{cedula}/imagen")
    public ResponseEntity<byte[]> obtenerImagenConsumidor(@PathVariable String cedula) {
        Consumidor consumidor = consumidorService.obtenerConsumidorPorCedula(cedula);

        if (consumidor.getRutaImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path rutaImagen = Paths.get(consumidor.getRutaImagen());
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


