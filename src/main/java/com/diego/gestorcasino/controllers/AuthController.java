package com.diego.gestorcasino.controllers;

import com.diego.gestorcasino.models.Usuario;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import com.diego.gestorcasino.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.diego.gestorcasino.repositories.AdministradorRepository;
import com.diego.gestorcasino.repositories.CajeroRepository;
import com.diego.gestorcasino.repositories.ContadorRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CajeroRepository cajeroRepository;

    @Autowired
    private ContadorRepository contadorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getCedula(), request.getPassword()
                    )
            );

            Usuario usuario = usuarioRepository.findByCedula(request.getCedula())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtUtil.generateToken(usuario.getCedula(), usuario.getRol().name());

            // Obtener datos específicos según el rol
            AuthResponse response = crearRespuestaSegunRol(usuario, token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    private AuthResponse crearRespuestaSegunRol(Usuario usuario, String token) {
        String cedula = usuario.getCedula();
        String email = usuario.getEmail();
        String rol = usuario.getRol().name();

        switch (usuario.getRol()) {
            case ADMIN:
                return administradorRepository.findById(cedula)
                        .map(admin -> new AuthResponse(
                                token, 
                                cedula, 
                                admin.getNombre(), 
                                email, 
                                admin.getTelefono(), 
                                rol
                        ))
                        .orElseThrow(() -> new RuntimeException("Datos de administrador no encontrados"));

            case CAJERO:
                return cajeroRepository.findById(cedula)
                        .map(cajero -> new AuthResponse(
                                token, 
                                cedula, 
                                cajero.getNombre(), 
                                email, 
                                cajero.getTelefono(), 
                                rol
                        ))
                        .orElseThrow(() -> new RuntimeException("Datos de cajero no encontrados"));

            case CONTADOR:
                return contadorRepository.findById(cedula)
                        .map(contador -> new AuthResponse(
                                token, 
                                cedula, 
                                contador.getNombre(), 
                                email, 
                                contador.getTelefono(), 
                                rol
                        ))
                        .orElseThrow(() -> new RuntimeException("Datos de contador no encontrados"));

            default:
                throw new RuntimeException("Rol no reconocido: " + usuario.getRol());
        }
    }

    // DTO interno
    public static class LoginRequest {
        private String cedula;
        private String password;

        public String getCedula() { return cedula; }
        public void setCedula(String cedula) { this.cedula = cedula; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        private String cedula;
        private String nombre;
        private String email;
        private String telefono;
        private String rol;

        public AuthResponse(String token, String cedula, String nombre, String email, String telefono, String rol) {
            this.token = token;
            this.cedula = cedula;
            this.nombre = nombre;
            this.email = email;
            this.telefono = telefono;
            this.rol = rol;
        }

        // Getters
        public String getToken() { return token; }
        public String getCedula() { return cedula; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public String getTelefono() { return telefono; }
        public String getRol() { return rol; }
    }
}