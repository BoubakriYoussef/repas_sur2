package com.example.repas_sur_backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_returnsSpringSecurityUser_withRoleAndEnabledFlag() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername("admin");
        utilisateur.setPassword("encoded");
        utilisateur.setRole(RoleUtilisateur.ADMIN);
        utilisateur.setActif(true);
        when(utilisateurRepository.findByUsername("admin")).thenReturn(Optional.of(utilisateur));

        var userDetails = customUserDetailsService.loadUserByUsername("admin");

        assertThat(userDetails.getUsername()).isEqualTo("admin");
        assertThat(userDetails.getPassword()).isEqualTo("encoded");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_throwsWhenUserDoesNotExist() {
        when(utilisateurRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("ghost"))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("ghost");
    }
}
