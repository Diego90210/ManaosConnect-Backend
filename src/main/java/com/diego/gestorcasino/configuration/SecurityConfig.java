package com.diego.gestorcasino.configuration;

import com.diego.gestorcasino.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; 
import org.springframework.web.cors.CorsConfigurationSource; 
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; 

import java.util.Arrays; 


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.admin-setup.enabled:true}")
    private boolean adminSetupEnabled;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // ← AGREGAR ESTA LÍNEA
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                // ENDPOINTS PÚBLICOS
                if (adminSetupEnabled) {
                    auth.requestMatchers("/api/setup/**").permitAll();
                }
                auth.requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/public/**").permitAll()
                    
                    // GESTIÓN DE USUARIOS - SOLO ADMIN
                    .requestMatchers("/admin/usuarios/**").hasRole("ADMIN")
                    .requestMatchers("/admin/administradores/**").hasRole("ADMIN")
                    .requestMatchers("/admin/cajeros/**").hasRole("ADMIN")  // Admin gestiona cajeros
                    .requestMatchers("/admin/contadores/**").hasRole("ADMIN")  // Admin gestiona contadores
                    
                    // GESTIÓN DE EMPRESAS Y CONSUMIDORES - SOLO ADMIN
                    .requestMatchers("/admin/empresas/**").hasRole("ADMIN")
                    .requestMatchers("/admin/consumidores/**").hasRole("ADMIN")
                    
                    // GESTIÓN DE PLATOS - SOLO ADMIN (con lectura para cajeros)
                    .requestMatchers("/admin/platos/**").hasRole("ADMIN")
                    .requestMatchers("/cajero/platos").hasAnyRole("ADMIN", "CAJERO")  // Solo lectura para cajeros
                    .requestMatchers("/cajero/platos/{id}").hasAnyRole("ADMIN", "CAJERO")  // Solo lectura para cajeros
                    
                    // GESTIÓN DE CONSUMOS - SOLO CAJERO (con lectura para admin y contador)
                    .requestMatchers("/cajero/consumos/**").hasAnyRole("ADMIN", "CAJERO")
                    .requestMatchers("/admin/consumos").hasRole("ADMIN")  // Admin puede ver todos
                    .requestMatchers("/admin/consumos/{id}").hasRole("ADMIN")  // Admin puede ver específicos
                    .requestMatchers("/contador/consumos").hasRole("CONTADOR")  // Contador puede ver para reportes
                    
                    // GESTIÓN DE REPORTES - SOLO CONTADOR (con lectura para admin)
                    .requestMatchers("/contador/reportes/**").hasAnyRole("ADMIN", "CONTADOR")
                    .requestMatchers("/admin/reportes").hasRole("ADMIN")  // Admin puede ver reportes
                    .requestMatchers("/admin/reportes/{id}").hasRole("ADMIN")  // Admin puede ver reportes específicos
                    
                    // ENDPOINTS DE CONSULTA PARA OPERACIONES TRANSVERSALES
                    .requestMatchers("/cajero/empresas").hasAnyRole("ADMIN", "CAJERO")  // Cajero necesita ver empresas para registrar consumos
                    .requestMatchers("/cajero/consumidores").hasAnyRole("ADMIN", "CAJERO")  // Cajero necesita ver consumidores
                    .requestMatchers("/contador/empresas").hasAnyRole("ADMIN", "CONTADOR")  // Contador necesita ver empresas para reportes
                    

                    .anyRequest().authenticated();
            })
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}