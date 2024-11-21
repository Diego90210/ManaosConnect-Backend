package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {

    @Autowired
    private AdministradorService administradorService;

    @PostMapping("/register")
    public ResponseEntity<?> registrarAdministrador(@RequestBody Administrador administrador) {
        try {
            Administrador nuevoAdministrador = administradorService.crearAdministrador(administrador);
            return ResponseEntity.ok(nuevoAdministrador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Administrador administrador) {
        if (administradorService.verificarCredenciales(administrador.getEmail(), administrador.getPassword())) {
            return ResponseEntity.ok("Inicio de sesión exitoso");
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
