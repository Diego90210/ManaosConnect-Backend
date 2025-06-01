package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public Usuario registrar(@RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    @PutMapping("/{cedula}")
    public Usuario actualizar(@PathVariable String cedula, @RequestBody Usuario usuario) {
        return usuarioService.actualizar(cedula, usuario);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<String> eliminar(@PathVariable String cedula) {
        try {
            usuarioService.eliminar(cedula);
            return ResponseEntity.ok("Usuario eliminado exitosamente (soft delete)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{cedula}")
    public Usuario obtener(@PathVariable String cedula) {
        return usuarioService.buscarPorCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario activo no encontrado con cédula: " + cedula));
    }

    // NUEVOS ENDPOINTS PARA GESTIÓN DE ELIMINADOS
    @GetMapping("/eliminados")
    public List<Usuario> listarEliminados() {
        return usuarioService.listarEliminados();
    }

    @GetMapping("/todos")
    public List<Usuario> listarTodosInclurandoEliminados() {
        return usuarioService.listarTodosIncluyendoEliminados();
    }

    @PutMapping("/{cedula}/reactivar")
    public ResponseEntity<String> reactivar(@PathVariable String cedula) {
        try {
            usuarioService.reactivarUsuario(cedula);
            return ResponseEntity.ok("Usuario reactivado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}