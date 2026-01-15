package com.example.repas_sur_backend.controller;

import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import com.example.repas_sur_backend.security.JwtService;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        UtilisateurRepository utilisateurRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Utilisateur utilisateur = utilisateurRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur not found: " + authentication.getName()));

        String token = jwtService.generateToken(
            utilisateur.getUsername(),
            Map.of("role", utilisateur.getRole().name())
        );

        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getRole().name()));
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record LoginResponse(String token, String role) {
    }
}
