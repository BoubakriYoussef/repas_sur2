package com.example.repas_sur_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        utilisateurRepository.deleteAll();
        Utilisateur user = new Utilisateur();
        user.setUsername("user1");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(RoleUtilisateur.ADMIN);
        user.setActif(true);
        utilisateurRepository.save(user);
    }

    @Test
    void login_returnsToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user1\",\"password\":\"password\"}"))
            .andExpect(status().isOk());
    }
}
