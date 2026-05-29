package com.healthdesk.service;

import com.healthdesk.dto.*;
import com.healthdesk.model.*;
import com.healthdesk.repository.UserRepository;
import com.healthdesk.security.JwtUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepo;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;

    @InjectMocks AuthService authService;

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Alice"); req.setEmail("alice@test.com");
        req.setPassword("secret123"); req.setRole(Role.PATIENT);

        when(userRepo.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(jwtUtil.generateToken(any(), any())).thenReturn("jwt-token");

        User saved = User.builder().id(1L).name("Alice")
            .email("alice@test.com").password("hashed").role(Role.PATIENT).build();
        when(userRepo.save(any())).thenReturn(saved);

        AuthResponse resp = authService.register(req);
        assertThat(resp.getToken()).isEqualTo("jwt-token");
        assertThat(resp.getEmail()).isEqualTo("alice@test.com");
    }

    @Test
    void register_duplicateEmail_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@test.com");
        when(userRepo.existsByEmail("dup@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already registered");
    }

    @Test
    void login_success() {
        AuthRequest req = new AuthRequest();
        req.setEmail("alice@test.com"); req.setPassword("secret123");

        User user = User.builder().id(1L).name("Alice")
            .email("alice@test.com").password("hashed").role(Role.PATIENT).build();
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("jwt-token");

        AuthResponse resp = authService.login(req);
        assertThat(resp.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_wrongPassword_throws() {
        AuthRequest req = new AuthRequest();
        req.setEmail("alice@test.com"); req.setPassword("wrong");

        User user = User.builder().email("alice@test.com").password("hashed").role(Role.PATIENT).build();
        when(userRepo.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid");
    }

    @Test
    void login_unknownEmail_throws() {
        AuthRequest req = new AuthRequest();
        req.setEmail("nobody@test.com"); req.setPassword("pw");
        when(userRepo.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
