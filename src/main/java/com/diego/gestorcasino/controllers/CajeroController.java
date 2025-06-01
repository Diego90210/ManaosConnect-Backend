package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.CajeroResponseDTO;
import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Cajero;
import com.diego.gestorcasino.services.CajeroService;
import com.diego.gestorcasino.services.UsuarioRolTransaccionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cajero/cajeros")
public class CajeroController {

    @Autowired
    private CajeroService cajeroService;

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolTransaccionalService;

    //Borrar de ser necesario
    @PostMapping
    public ResponseEntity<Cajero> registrar(@RequestBody Cajero cajero) {
        Cajero nuevo = cajeroService.guardar(cajero);
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
    public ResponseEntity<Cajero> actualizar(@PathVariable String cedula, @RequestBody Cajero cajero) {
        Cajero actualizado = cajeroService.actualizar(cedula, cajero);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminar(@PathVariable String cedula) {
        cajeroService.eliminar(cedula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CajeroResponseDTO>> listarTodos() {
        System.out.println("=== ENTRANDO A LISTAR CAJEROS ===");
        System.out.println("Usuario autenticado: " + SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Autoridades: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        List<Cajero> cajeros = cajeroService.listarTodos();
        List<CajeroResponseDTO> response = cajeros.stream()
                .map(c -> new CajeroResponseDTO(
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
    public ResponseEntity<Cajero> obtener(@PathVariable String cedula) {
        return cajeroService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado con cédula: " + cedula));
    }
}