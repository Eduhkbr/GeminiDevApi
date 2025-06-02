package com.eduhkbr.gemini.DevApi.config;

import com.eduhkbr.gemini.DevApi.model.User;
import com.eduhkbr.gemini.DevApi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    @Mock
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("Deve criar bean PasswordEncoder do tipo BCryptPasswordEncoder")
    void testPasswordEncoderBean() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String senha = "123456";
        String hash = encoder.encode(senha);
        assertThat(encoder.matches(senha, hash)).isTrue();
    }

    @Test
    @DisplayName("Deve criar bean UserDetailsService que retorna usuário existente")
    void testUserDetailsServiceReturnsUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        UserDetails details = securityConfig.userDetailsService(userRepository).loadUserByUsername("admin");
        assertThat(details.getUsername()).isEqualTo("admin");
        assertThat(details.getPassword()).isEqualTo("hash");
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException se usuário não existe")
    void testUserDetailsServiceThrowsIfUserNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> securityConfig.userDetailsService(userRepository).loadUserByUsername("notfound"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve criar bean SecurityFilterChain sem exceção")
    void testSecurityFilterChainBean() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, RETURNS_DEEP_STUBS);
        // Não testamos o comportamento interno do HttpSecurity, apenas se o bean é criado sem erro
        SecurityFilterChain chain = securityConfig.filterChain(http);
        assertThat(chain).isNotNull();
    }
}
