package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void eliminar(@PathVariable String cedula) {
        usuarioService.eliminar(cedula);
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    @GetMapping("/{cedula}")
    public Usuario obtener(@PathVariable String cedula) {
        return usuarioService.buscarPorCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
    }
}
