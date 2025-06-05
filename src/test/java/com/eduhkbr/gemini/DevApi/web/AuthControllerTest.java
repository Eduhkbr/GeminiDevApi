package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.User;
import com.eduhkbr.gemini.DevApi.repository.UserRepository;
import com.eduhkbr.gemini.DevApi.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("login - sucesso retorna token")
    void login_quandoCredenciaisValidas_entaoRetornaToken() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("token.jwt");

        ResponseEntity<?> response = authController.login(Map.of("username", "admin", "password", "123"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> bodyOk = (Map<?, ?>) response.getBody();
        assertThat(bodyOk != null && "token.jwt".equals(bodyOk.get("token"))).isTrue();
    }

    @Test
    @DisplayName("login - usuário não encontrado retorna 401")
    void login_quandoUsuarioNaoExiste_entaoRetorna401() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        ResponseEntity<?> response = authController.login(Map.of("username", "notfound", "password", "qualquer"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> bodyNotFound = (Map<?, ?>) response.getBody();
        assertThat(bodyNotFound != null && bodyNotFound.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - senha inválida retorna 401")
    void login_quandoSenhaInvalida_entaoRetorna401() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("errada", "hash")).thenReturn(false);
        ResponseEntity<?> response = authController.login(Map.of("username", "admin", "password", "errada"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> bodyInvalid = (Map<?, ?>) response.getBody();
        assertThat(bodyInvalid != null && bodyInvalid.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - username nulo retorna 401")
    void login_quandoUsernameNulo_entaoRetorna401() {
        ResponseEntity<?> response = authController.login(Map.of("password", "123"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body != null && body.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - password nulo retorna 401")
    void login_quandoPasswordNulo_entaoRetorna401() {
        ResponseEntity<?> response = authController.login(Map.of("username", "admin"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body != null && body.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - username em branco retorna 401")
    void login_quandoUsernameEmBranco_entaoRetorna401() {
        ResponseEntity<?> response = authController.login(Map.of("username", " ", "password", "123"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body != null && body.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - password em branco retorna 401")
    void login_quandoPasswordEmBranco_entaoRetorna401() {
        ResponseEntity<?> response = authController.login(Map.of("username", "admin", "password", " "));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(Map.class);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body != null && body.get("error") != null).isTrue();
    }

    @Test
    @DisplayName("login - username com caracteres especiais é sanitizado")
    void login_quandoUsernameComCaracteresEspeciais_entaoSanitiza() {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash("hash");
        user.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "hash")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ROLE_ADMIN")).thenReturn("token.jwt");
        ResponseEntity<?> response = authController.login(Map.of("username", "ad<min>", "password", "123"));
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
        Map<?, ?> bodyOk = (Map<?, ?>) response.getBody();
        assertThat(bodyOk != null && "token.jwt".equals(bodyOk.get("token"))).isTrue();
    }
}
