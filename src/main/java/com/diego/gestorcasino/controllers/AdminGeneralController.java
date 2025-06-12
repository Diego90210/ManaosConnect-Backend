package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.dto.ConsumoDTO;
import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.dto.ReporteResponseDTO;
import com.diego.gestorcasino.models.*;
import com.diego.gestorcasino.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminGeneralController {

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolService;
    
    @Autowired
    private EmpresaClienteService empresaService;
    
    @Autowired
    private ConsumidorService consumidorService;
    
    @Autowired
    private PlatoService platoService;
    
    @Autowired
    private ConsumoService consumoService;
    
    @Autowired
    private ReporteService reporteService;

    //  GESTIÓN DE USUARIOS (Solo Admin)
    @PostMapping("/usuarios/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody RegistroUsuarioRequest request) {
        try {
            usuarioRolService.registrarUsuarioCompleto(request);
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/usuarios/{cedula}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable String cedula) {
        try {
            usuarioRolService.eliminarUsuarioCompleto(cedula);
            return ResponseEntity.ok("Usuario eliminado exitosamente (soft delete)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/usuarios/reactivar/{cedula}")
    public ResponseEntity<String> reactivarUsuario(@PathVariable String cedula) {
        try {
            usuarioRolService.reactivarUsuario(cedula);
            return ResponseEntity.ok("Usuario reactivado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    //  GESTIÓN DE EMPRESAS (Solo Admin)
    @PostMapping("/empresas")
    public ResponseEntity<EmpresaCliente> crearEmpresa(@RequestBody EmpresaCliente empresa) {
        return ResponseEntity.ok(empresaService.guardar(empresa));
    }

    @PutMapping("/empresas/{nit}")
    public ResponseEntity<EmpresaCliente> actualizarEmpresa(@PathVariable String nit, @RequestBody EmpresaCliente empresa) {
        return ResponseEntity.ok(empresaService.actualizar(nit, empresa));
    }

    @DeleteMapping("/empresas/{nit}")
    public ResponseEntity<String> eliminarEmpresa(@PathVariable String nit) {
        try {
            empresaService.eliminar(nit);
            return ResponseEntity.ok("Empresa eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaCliente>> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    //  GESTIÓN DE CONSUMIDORES (Solo Admin)
    @PostMapping("/consumidores")
    public ResponseEntity<Consumidor> crearConsumidor(@RequestBody Consumidor consumidor) {
        return ResponseEntity.ok(consumidorService.guardar(consumidor));
    }

    @PutMapping("/consumidores/{cedula}")
    public ResponseEntity<Consumidor> actualizarConsumidor(@PathVariable String cedula, @RequestBody Consumidor consumidor) {
        return ResponseEntity.ok(consumidorService.actualizar(cedula, consumidor));
    }

    @DeleteMapping("/consumidores/{cedula}")
    public ResponseEntity<String> eliminarConsumidor(@PathVariable String cedula) {
        try {
            consumidorService.eliminar(cedula);
            return ResponseEntity.ok("Consumidor eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/consumidores")
    public ResponseEntity<List<Consumidor>> listarConsumidores() {
        return ResponseEntity.ok(consumidorService.listarTodos());
    }

    //  GESTIÓN DE PLATOS (Solo Admin)
    @PostMapping("/platos")
    public ResponseEntity<Plato> crearPlato(@RequestBody Plato plato) {
        return ResponseEntity.ok(platoService.guardar(plato));
    }

    @PutMapping("/platos/{id}")
    public ResponseEntity<Plato> actualizarPlato(@PathVariable int id, @RequestBody Plato plato) {
        return ResponseEntity.ok(platoService.actualizar(id, plato));
    }

    @DeleteMapping("/platos/{id}")
    public ResponseEntity<String> eliminarPlato(@PathVariable int id) {
        try {
            platoService.eliminar(id);
            return ResponseEntity.ok("Plato eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/platos")
    public ResponseEntity<List<Plato>> listarPlatos() {
        return ResponseEntity.ok(platoService.listarTodos());
    }

    //  CONSULTA DE CONSUMOS (Solo lectura para Admin)
    @GetMapping("/consumos")
    public ResponseEntity<List<ConsumoDTO>> listarTodosConsumos() {
        return ResponseEntity.ok(consumoService.obtenerTodosLosConsumos()); // ✅ Ya usa DTOs
    }

    @GetMapping("/consumos/{id}")
    public ResponseEntity<ConsumoDTO> obtenerConsumo(@PathVariable int id) {
        ConsumoDTO consumo = consumoService.obtenerConsumoPorId(id); // ✅ Ya usa DTOs
        return ResponseEntity.ok(consumo);
    }

    //  CONSULTA DE REPORTES (Solo lectura para Admin) - USAR DTOs
    @GetMapping("/reportes")
    public ResponseEntity<List<ReporteResponseDTO>> listarTodosReportes() {
        return ResponseEntity.ok(reporteService.listarTodosDTO()); //  CAMBIO A DTO
    }

    @GetMapping("/reportes/{id}")
    public ResponseEntity<ReporteResponseDTO> obtenerReporte(@PathVariable int id) {
        ReporteResponseDTO reporte = reporteService.obtenerPorIdDTO(id); //  CAMBIO A DTO
        return ResponseEntity.ok(reporte);
    }
}