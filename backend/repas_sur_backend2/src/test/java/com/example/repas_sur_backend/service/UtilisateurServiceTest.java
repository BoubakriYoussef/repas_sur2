package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.UtilisateurRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import java.util.List;
import java.util.Optional;
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
        when(utilisateurRepository.save(org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        utilisateurService.save(request);

        org.mockito.Mockito.verify(passwordEncoder).encode("secret");
    }

    @Test
    void save_throwsWhenSiteDoesNotExist() {
        UtilisateurRequest request = new UtilisateurRequest(
            "admin",
            "secret",
            "admin@safemeal.local",
            null,
            null,
            RoleUtilisateur.ADMIN,
            true,
            44L
        );
        when(siteRepository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> utilisateurService.save(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Site not found: 44");
        verify(utilisateurRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void update_keepsExistingPasswordWhenBlankPasswordProvided() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(5L);
        utilisateur.setUsername("old");
        utilisateur.setPassword("kept-password");
        utilisateur.setRole(RoleUtilisateur.CUISINE);
        utilisateur.setActif(true);
        when(utilisateurRepository.findById(5L)).thenReturn(Optional.of(utilisateur));
        when(utilisateurRepository.save(utilisateur)).thenReturn(utilisateur);

        UtilisateurRequest request = new UtilisateurRequest(
            "new-name",
            " ",
            "user@test.local",
            "0102",
            "Chef",
            RoleUtilisateur.RESPONSABLE,
            false,
            null
        );

        var result = utilisateurService.update(5L, request);

        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        assertThat(utilisateur.getPassword()).isEqualTo("kept-password");
        assertThat(result.username()).isEqualTo("new-name");
        assertThat(result.role()).isEqualTo(RoleUtilisateur.RESPONSABLE);
    }

    @Test
    void getById_mapsSiteWhenPresent() {
        SiteRestauration site = new SiteRestauration();
        site.setId(9L);
        site.setNom("Site A");
        site.setAdresse("Rue 1");
        site.setType(TypeSite.SCOLAIRE);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(7L);
        utilisateur.setUsername("admin");
        utilisateur.setEmail("admin@test.local");
        utilisateur.setRole(RoleUtilisateur.ADMIN);
        utilisateur.setActif(true);
        utilisateur.setSite(site);
        when(utilisateurRepository.findById(7L)).thenReturn(Optional.of(utilisateur));

        var result = utilisateurService.getById(7L);

        assertThat(result.site()).isEqualTo(new SiteRestaurationDto(9L, "Site A", TypeSite.SCOLAIRE, "Rue 1"));
    }

    @Test
    void findAll_returnsEmptyListWhenRepositoryIsEmpty() {
        when(utilisateurRepository.findAll()).thenReturn(List.of());

        assertThat(utilisateurService.findAll()).isEmpty();
    }
}
