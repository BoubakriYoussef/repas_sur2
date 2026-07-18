package com.example.repas_sur_backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "dGVzdC1qd3Qtc2VjcmV0LWZvci1jaS1hbmQtdGVzdHMtYmFzZTY0LXN0cmluZw==";

    @Test
    void generateToken_extractsUsername_andValidatesToken() {
        JwtService jwtService = new JwtService(SECRET, 60_000);

        String token = jwtService.generateToken("alice", Map.of("role", "ADMIN"));

        assertThat(jwtService.extractUsername(token)).isEqualTo("alice");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_returnsFalse_forMalformedToken() {
        JwtService jwtService = new JwtService(SECRET, 60_000);

        assertThat(jwtService.isTokenValid("not-a-jwt")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalse_forExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, -1);
        String token = jwtService.generateToken("alice", Map.of());

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }
}
