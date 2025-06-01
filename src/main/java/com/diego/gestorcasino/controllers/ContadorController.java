package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.ContadorResponseDTO;
import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Contador;
import com.diego.gestorcasino.services.ContadorService;
import com.diego.gestorcasino.services.UsuarioRolTransaccionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contador/contadores")
public class ContadorController {

    @Autowired
    private ContadorService contadorService;

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolTransaccionalService;

    @PostMapping
    public ResponseEntity<Contador> registrar(@RequestBody Contador contador) {
        Contador nuevo = contadorService.guardar(contador);
        return ResponseEntity.ok(nuevo);
    }

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
    public ResponseEntity<Contador> actualizar(@PathVariable String cedula, @RequestBody Contador contador) {
        Contador actualizado = contadorService.actualizar(cedula, contador);
        return ResponseEntity.ok(actualizado);
    }

    // USAR SOFT DELETE
    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminar(@PathVariable String cedula) {
        try {
            contadorService.eliminar(cedula);
            return ResponseEntity.ok("Contador eliminado exitosamente (soft delete)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // SOLO CONTADORES ACTIVOS
    @GetMapping
    public ResponseEntity<List<ContadorResponseDTO>> listarTodos() {
        System.out.println("=== ENTRANDO A LISTAR CONTADORES ===");
        System.out.println("Usuario autenticado: " + SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("Autoridades: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        List<Contador> contadores = contadorService.listarTodos();
        List<ContadorResponseDTO> response = contadores.stream()
            .map(c -> new ContadorResponseDTO(
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
    public ResponseEntity<Contador> obtener(@PathVariable String cedula) {
        return contadorService.buscarPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Contador activo no encontrado con cédula: " + cedula));
    }
}