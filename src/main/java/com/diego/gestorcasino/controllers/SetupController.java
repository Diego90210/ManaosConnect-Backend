package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.services.SetupInicialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    @Autowired
    private SetupInicialService setupService;

    @GetMapping("/estado")
    public ResponseEntity<?> verificarEstado() {
        boolean necesitaSetup = setupService.necesitaSetupInicial();
        return ResponseEntity.ok().body(new EstadoSetupResponse(necesitaSetup));
    }

    @PostMapping("/admin-inicial")
    public ResponseEntity<?> crearAdminInicial(@RequestBody RegistroUsuarioRequest request) {
        try {
            setupService.crearAdministradorInicial(request);
            return ResponseEntity.ok("Administrador inicial creado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/admin-por-defecto")
    public ResponseEntity<?> crearAdminPorDefecto() {
        try {
            setupService.crearAdministradorPorDefecto();
            return ResponseEntity.ok("Administrador por defecto creado. Cédula: 00000000, Password: admin123");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // Clase interna para respuesta
    public static class EstadoSetupResponse {
        private boolean necesitaSetupInicial;
        private String mensaje;

        public EstadoSetupResponse(boolean necesitaSetupInicial) {
            this.necesitaSetupInicial = necesitaSetupInicial;
            this.mensaje = necesitaSetupInicial ? 
                "Sistema requiere configuración inicial" : 
                "Sistema ya configurado";
        }

        // Getters
        public boolean isNecesitaSetupInicial() { return necesitaSetupInicial; }
        public String getMensaje() { return mensaje; }
    }
}