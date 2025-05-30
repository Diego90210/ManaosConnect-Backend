package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Contador;
import com.diego.gestorcasino.services.ContadorService;
import com.diego.gestorcasino.services.UsuarioRolTransaccionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contador/contadores")
public class ContadorController {

    @Autowired
    private ContadorService contadorService;

    //Borrar de ser necesario
    @PostMapping
    public ResponseEntity<Contador> registrar(@RequestBody Contador contador) {
        Contador nuevo = contadorService.guardar(contador);
        return ResponseEntity.ok(nuevo);
    }

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolTransaccionalService; // Cambio aquí

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistroUsuarioRequest request) {
        try {
            usuarioRolTransaccionalService.registrarUsuarioCompleto(request); // Usar inyección
            return ResponseEntity.ok("Usuario registrado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{cedula}")
    public ResponseEntity<Contador> actualizar(@PathVariable String cedula, @RequestBody Contador contador) {
        Contador actualizado = contadorService.actualizar(cedula, contador);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminar(@PathVariable String cedula) {
        contadorService.eliminar(cedula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Contador>> listarTodos() {
        return ResponseEntity.ok(contadorService.listarTodos());
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<Contador> obtener(@PathVariable String cedula) {
        return contadorService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Contador no encontrado con cédula: " + cedula));
    }
}

