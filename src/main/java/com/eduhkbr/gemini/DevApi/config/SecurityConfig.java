package com.eduhkbr.gemini.DevApi.config;

import com.eduhkbr.gemini.DevApi.model.User;
import com.eduhkbr.gemini.DevApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    /**
     * Configuração de segurança da aplicação utilizando sintaxe moderna do Spring Security 6+.
     * Permite acesso público à documentação Swagger e exige autenticação básica para demais endpoints.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Necessário se usar H2-console ou UIs embutidas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/api/auth/login",
                    "/login.html", "/app.html", "/app.js", "/favicon.ico", "/css/**", "/js/**", "/static/**",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // Redireciona para login.html se não autenticado e for acesso à raiz ou páginas estáticas
                    String uri = request.getRequestURI();
                    if (uri.equals("/") || uri.equals("") || uri.equals("/index.html")) {
                        response.sendRedirect("/login.html");
                    } else {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Não autenticado\"}");
                    }
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
