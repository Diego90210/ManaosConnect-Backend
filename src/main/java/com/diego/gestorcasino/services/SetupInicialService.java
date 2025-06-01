package com.diego.gestorcasino.services;

import com.diego.gestorcasino.dto.RegistroUsuarioRequest;
import com.diego.gestorcasino.models.Rol;
import com.diego.gestorcasino.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetupInicialService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioRolTransaccionalService usuarioRolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin-setup.enabled:true}")
    private boolean adminSetupEnabled;

    @Value("${app.admin-setup.default-password:admin123}")
    private String defaultAdminPassword;

    public boolean necesitaSetupInicial() {
        return adminSetupEnabled && !existeAdministrador();
    }

    // SOLO CONTAR ADMINISTRADORES ACTIVOS
    public boolean existeAdministrador() {
        return usuarioRepository.countActiveByRol(Rol.ADMIN) > 0;
    }

    @Transactional
    public void crearAdministradorInicial(RegistroUsuarioRequest request) {
        if (!adminSetupEnabled) {
            throw new RuntimeException("El setup inicial está deshabilitado");
        }

        if (existeAdministrador()) {
            throw new RuntimeException("Ya existe un administrador activo en el sistema");
        }

        // Forzar rol ADMIN
        request.setRol(Rol.ADMIN);
        
        // Validaciones básicas
        if (request.getCedula() == null || request.getCedula().trim().isEmpty()) {
            throw new IllegalArgumentException("La cédula es obligatoria");
        }
        
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            request.setPassword(defaultAdminPassword);
        }

        usuarioRolService.registrarUsuarioCompleto(request);
    }

    @Transactional
    public void crearAdministradorPorDefecto() {
        if (necesitaSetupInicial()) {
            RegistroUsuarioRequest request = new RegistroUsuarioRequest();
            request.setCedula("00000000");
            request.setEmail("admin@gestorcasino.com");
            request.setPassword(defaultAdminPassword);
            request.setNombre("Administrador Principal");
            request.setTelefono("000-000-0000");
            request.setRol(Rol.ADMIN);

            crearAdministradorInicial(request);
        }
    }
}