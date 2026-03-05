package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.AlerteRisqueDto;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.ConviveRepository;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
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
    void genererAlertesPourService_returnsEmptyWhenAlreadyGenerated() {
        when(alerteRisqueRepository.existsByServiceId(1L)).thenReturn(true);

        List<AlerteRisqueDto> result = alerteRisqueService.genererAlertesPourService(1L);

        assertThat(result).isEmpty();
        verify(alerteRisqueRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void genererAlertesPourService_createsAllergeneAlert() {
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

        SiteRestauration site = new SiteRestauration();
        site.setId(3L);
        site.setNom("Site");
        site.setConvives(Set.of(convive));

        ServiceRepas service = new ServiceRepas();
        service.setId(1L);
        service.setSite(site);
        service.setMenu(menu);

        when(alerteRisqueRepository.existsByServiceId(1L)).thenReturn(false);
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
}

