package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.diego.gestorcasino.services.UsuarioRolTransaccionalService;

import java.util.List;

@RestController
@RequestMapping("/admin/administradores")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolTransaccionalService;

    //Borrar de ser necesario
    @PostMapping
    public ResponseEntity<Administrador> registrar(@RequestBody Administrador administrador) {
        Administrador nuevo = administradorService.guardar(administrador);
        return ResponseEntity.ok(nuevo);
    }

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
    public ResponseEntity<Administrador> actualizar(@PathVariable String cedula, @RequestBody Administrador admin) {
        Administrador actualizado = administradorService.actualizar(cedula, admin);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminar(@PathVariable String cedula) {
        administradorService.eliminar(cedula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> listarTodos() {
        return ResponseEntity.ok(administradorService.listarTodos());
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<Administrador> obtener(@PathVariable String cedula) {
        return administradorService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con cédula: " + cedula));
    }
}
