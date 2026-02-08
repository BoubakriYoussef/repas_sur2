package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.UtilisateurRequest;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private SiteRestaurationRepository siteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @Test
    void save_requiresPassword() {
        UtilisateurRequest request = new UtilisateurRequest(
            "admin",
            null,
            "admin@safemeal.local",
            null,
            null,
            RoleUtilisateur.ADMIN,
            true,
            null
        );

        assertThatThrownBy(() -> utilisateurService.save(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Password is required");
    }

    @Test
    void save_encodesPassword() {
        UtilisateurRequest request = new UtilisateurRequest(
            "admin",
            "secret",
            "admin@safemeal.local",
            null,
            null,
            RoleUtilisateur.ADMIN,
            true,
            null
        );

        when(passwordEncoder.encode("secret")).thenReturn("encoded");

        utilisateurService.save(request);

        org.mockito.Mockito.verify(passwordEncoder).encode("secret");
    }
}
