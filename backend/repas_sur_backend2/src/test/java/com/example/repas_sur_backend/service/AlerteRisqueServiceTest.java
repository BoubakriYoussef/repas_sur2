package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.AlerteEtatUpdateRequest;
import com.example.repas_sur_backend.dto.AlerteRisqueDto;
import com.example.repas_sur_backend.dto.AlerteRisqueRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.ConviveRepository;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlerteRisqueServiceTest {

    @Mock
    private AlerteRisqueRepository alerteRisqueRepository;

    @Mock
    private ServiceRepasRepository serviceRepasRepository;

    @Mock
    private ConviveRepository conviveRepository;

    @Mock
    private AllergeneRepository allergeneRepository;

    @InjectMocks
    private AlerteRisqueService alerteRisqueService;

    @Test
    void genererAlertesPourService_skipsExistingConviveAndCreatesMissingAlert() {
        Allergene gluten = new Allergene();
        gluten.setId(1L);
        gluten.setCode("GLUTEN");
        gluten.setLibelle("Gluten");

        Plat plat = new Plat();
        plat.setNom("Gratin");
        plat.setContientPorc(false);
        plat.setEstVegetarien(true);
        plat.setAllergenes(Set.of(gluten));

        Menu menu = new Menu();
        menu.setId(9L);
        menu.setNom("Menu A");
        menu.setPlats(Set.of(plat));

        Convive convive = new Convive();
        convive.setId(5L);
        convive.setNom("Martin");
        convive.setPrenom("Lea");
        convive.setAllergenes(Set.of(gluten));
        convive.setRegimes(Set.of());

        Convive dejaTraite = new Convive();
        dejaTraite.setId(4L);
        dejaTraite.setNom("Dupont");
        dejaTraite.setPrenom("Emma");
        dejaTraite.setAllergenes(Set.of(gluten));
        dejaTraite.setRegimes(Set.of());

        SiteRestauration site = new SiteRestauration();
        site.setId(3L);
        site.setNom("Site");
        site.setConvives(Set.of(dejaTraite, convive));

        ServiceRepas service = new ServiceRepas();
        service.setId(1L);
        service.setSite(site);
        service.setMenu(menu);

        when(alerteRisqueRepository.existsByServiceIdAndConviveId(1L, 4L)).thenReturn(true);
        when(alerteRisqueRepository.existsByServiceIdAndConviveId(1L, 5L)).thenReturn(false);
        when(serviceRepasRepository.findById(1L)).thenReturn(Optional.of(service));
        when(alerteRisqueRepository.save(org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> {
                com.example.repas_sur_backend.model.AlerteRisque entity = invocation.getArgument(0);
                entity.setId(20L);
                return entity;
            });

        List<AlerteRisqueDto> result = alerteRisqueService.genererAlertesPourService(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(20L);
        assertThat(result.get(0).niveau()).isEqualTo(NiveauAlerte.FORT);
        assertThat(result.get(0).message()).contains("GLUTEN");
        assertThat(result.get(0).allergenes()).hasSize(1);
    }

    @Test
    void genererAlertesPourService_returnsEmptyWhenServiceHasNoMenu() {
        ServiceRepas service = new ServiceRepas();
        service.setId(1L);
        service.setSite(new SiteRestauration());
        when(serviceRepasRepository.findById(1L)).thenReturn(Optional.of(service));

        assertThat(alerteRisqueService.genererAlertesPourService(1L)).isEmpty();
        verify(alerteRisqueRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void genererAlertesPourService_createsRegimeAlertWithoutAllergenes() {
        Regime regime = new Regime();
        regime.setCode("SANS_PORC");

        Plat plat = new Plat();
        plat.setNom("Plat porc");
        plat.setContientPorc(true);
        plat.setEstVegetarien(true);
        plat.setAllergenes(Set.of());

        Menu menu = new Menu();
        menu.setPlats(Set.of(plat));

        Convive convive = new Convive();
        convive.setId(6L);
        convive.setNom("Durand");
        convive.setPrenom("Leo");
        convive.setAllergenes(Set.of());
        convive.setRegimes(Set.of(regime));

        SiteRestauration site = new SiteRestauration();
        site.setConvives(Set.of(convive));

        ServiceRepas service = new ServiceRepas();
        service.setId(2L);
        service.setMenu(menu);
        service.setSite(site);

        when(alerteRisqueRepository.existsByServiceIdAndConviveId(2L, 6L)).thenReturn(false);
        when(serviceRepasRepository.findById(2L)).thenReturn(Optional.of(service));
        when(alerteRisqueRepository.save(org.mockito.ArgumentMatchers.any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<AlerteRisqueDto> result = alerteRisqueService.genererAlertesPourService(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).niveau()).isEqualTo(NiveauAlerte.MOYEN);
        assertThat(result.get(0).message()).contains("Regime incompatible");
        assertThat(result.get(0).allergenes()).isEmpty();
    }

    @Test
    void save_throwsWhenConviveDoesNotExist() {
        AlerteRisqueRequest request = new AlerteRisqueRequest(
            EtatAlerte.NOUVELLE,
            NiveauAlerte.FORT,
            "msg",
            LocalDateTime.of(2026, 7, 17, 10, 0),
            7L,
            8L,
            Set.of(1L)
        );
        when(conviveRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alerteRisqueService.save(request))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("Convive not found: 7");
        verify(alerteRisqueRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateEtat_updatesStateAndReturnsDto() {
        Convive convive = new Convive();
        convive.setId(1L);
        convive.setNom("Doe");
        convive.setPrenom("Jane");
        AlerteRisque alerte = new AlerteRisque();
        alerte.setId(9L);
        alerte.setEtat(EtatAlerte.NOUVELLE);
        alerte.setNiveau(NiveauAlerte.FAIBLE);
        alerte.setMessage("msg");
        alerte.setDateCreation(LocalDateTime.of(2026, 7, 17, 12, 0));
        alerte.setConvive(convive);
        when(alerteRisqueRepository.findById(9L)).thenReturn(Optional.of(alerte));
        when(alerteRisqueRepository.save(alerte)).thenReturn(alerte);

        AlerteRisqueDto result = alerteRisqueService.updateEtat(9L, new AlerteEtatUpdateRequest(EtatAlerte.TRAITEE));

        assertThat(result.etat()).isEqualTo(EtatAlerte.TRAITEE);
    }
}

