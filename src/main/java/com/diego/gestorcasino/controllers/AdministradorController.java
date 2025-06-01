package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.AdministradorResponseDTO;
import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Administrador;
import com.diego.gestorcasino.services.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostMapping("/registrar")
    public ResponseEntity<String> registrar(@RequestBody RegistroUsuarioRequest request) {
        try {
            usuarioRolTransaccionalService.registrarUsuarioCompleto(request);
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

    //USAR SOFT DELETE
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminar(@PathVariable String cedula) {
        try {
            administradorService.eliminar(cedula);
            return ResponseEntity.ok("Administrador eliminado exitosamente (soft delete)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // SOLO ADMINISTRADORES ACTIVOS
    @GetMapping
    public ResponseEntity<List<AdministradorResponseDTO>> listarTodos() {
        System.out.println("=== ENTRANDO A LISTAR ADMINISTRADORES ===");
        System.out.println("Usuario autenticado: " + SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Autoridades: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        List<Administrador> administradores = administradorService.listarTodos();
        List<AdministradorResponseDTO> response = administradores.stream()
                .map(c -> new AdministradorResponseDTO(
                        c.getCedula(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getUsuario() != null ? c.getUsuario().getEmail() : null
                ))
                .toList();

        System.out.println("Cantidad encontrada: " + response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<Administrador> obtener(@PathVariable String cedula) {
        return administradorService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Administrador activo no encontrado con cédula: " + cedula));
    }
}