package com.relatorios.relatorios.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Configura o PasswordEncoder customizado para SHA1
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new SHA1PasswordEncoder();
    }

    /**
     * Configura o AuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configura o AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura as regras de segurança HTTP
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Permite acesso a recursos estáticos
                        .requestMatchers("/login").permitAll() // Permite acesso à página de login
                        .anyRequest().authenticated() // Exige autenticação para todas as outras URLs
                )
                .formLogin(form -> form
                        .loginPage("/login") // Página customizada de login
                        .loginProcessingUrl("/login") // URL que processa o login
                        .defaultSuccessUrl("/dashboard", true) // Redireciona para dashboard após login
                        .failureUrl("/login?error=true") // Redireciona para login com erro
                        .usernameParameter("email") // Nome do campo de email no form
                        .passwordParameter("senha") // Nome do campo de senha no form
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para fazer logout
                        .logoutSuccessUrl("/login?logout=true") // Redireciona após logout
                        .invalidateHttpSession(true) // Invalida a sessão
                        .deleteCookies("JSESSIONID") // Remove o cookie de sessão
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**") // Desabilita CSRF para API (se houver)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // Permite apenas uma sessão por usuário
                        .maxSessionsPreventsLogin(false) // Permite login mesmo com sessão ativa (encerra a anterior)
                );

        return http.build();
    }
}