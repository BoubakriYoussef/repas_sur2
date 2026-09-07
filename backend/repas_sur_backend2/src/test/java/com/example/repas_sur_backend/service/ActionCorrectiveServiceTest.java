package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.ActionCorrectiveDto;
import com.example.repas_sur_backend.dto.ActionCorrectiveRequest;
import com.example.repas_sur_backend.exception.ConflictException;
import com.example.repas_sur_backend.model.ActionCorrective;
import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.repository.ActionCorrectiveRepository;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActionCorrectiveServiceTest {

    @Mock
    private ActionCorrectiveRepository actionCorrectiveRepository;

    @Mock
    private AlerteRisqueRepository alerteRisqueRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private ActionCorrectiveService actionCorrectiveService;

    @Test
    void save_persistsActionCorrective() {
        Convive convive = new Convive();
        convive.setId(2L);
        convive.setNom("Doe");
        convive.setPrenom("Jane");

        AlerteRisque alerte = new AlerteRisque();
        alerte.setId(8L);
        alerte.setConvive(convive);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(4L);
        utilisateur.setUsername("admin");
        utilisateur.setRole(RoleUtilisateur.ADMIN);
        utilisateur.setActif(true);

        when(alerteRisqueRepository.findById(8L)).thenReturn(Optional.of(alerte));
        when(utilisateurRepository.findByUsername("admin")).thenReturn(Optional.of(utilisateur));
        when(actionCorrectiveRepository.save(org.mockito.ArgumentMatchers.any(ActionCorrective.class)))
            .thenAnswer(invocation -> {
                ActionCorrective entity = invocation.getArgument(0);
                entity.setId(15L);
                return entity;
            });

        ActionCorrectiveRequest request = new ActionCorrectiveRequest(
            LocalDateTime.of(2026, 3, 4, 10, 0),
            "REMPLACEMENT_MENU",
            "Remplacement sans allergene",
            8L
        );

        ActionCorrectiveDto result = actionCorrectiveService.save(request, "admin");

        assertThat(result.id()).isEqualTo(15L);
        assertThat(result.alerte()).isNotNull();
        assertThat(result.alerte().id()).isEqualTo(8L);
        assertThat(result.utilisateur()).isNotNull();
        assertThat(result.utilisateur().username()).isEqualTo("admin");
    }

    @Test
    void save_refusesSecondActionForSameAlert() {
        ActionCorrectiveRequest request = new ActionCorrectiveRequest(
            LocalDateTime.of(2026, 3, 4, 10, 0),
            "REMPLACEMENT_MENU",
            "Deja traitee",
            8L
        );
        when(actionCorrectiveRepository.existsByAlerteId(8L)).thenReturn(true);

        assertThatThrownBy(() -> actionCorrectiveService.save(request, "admin"))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Une action corrective existe deja pour cette alerte");
    }
}

