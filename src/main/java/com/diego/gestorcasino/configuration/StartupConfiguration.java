package com.diego.gestorcasino.configuration;

import com.diego.gestorcasino.services.SetupInicialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfiguration {

    @Value("${app.admin-setup.auto-create:false}")
    private boolean autoCreateAdmin;

    @Autowired
    private SetupInicialService setupService;

    @Bean
    public ApplicationRunner setupRunner() {
        return args -> {
            if (autoCreateAdmin && setupService.necesitaSetupInicial()) {
                System.out.println("=== CREANDO ADMINISTRADOR POR DEFECTO ===");
                setupService.crearAdministradorPorDefecto();
                System.out.println("Administrador creado - Cédula: 00000000, Password: admin123");
                System.out.println("¡CAMBIA ESTAS CREDENCIALES INMEDIATAMENTE!");
            }
        };
    }
}