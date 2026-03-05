package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.ConviveDto;
import com.example.repas_sur_backend.dto.ConviveRequest;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.RegimeType;
import com.example.repas_sur_backend.model.enums.TypeConvive;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.ConviveRepository;
import com.example.repas_sur_backend.repository.RegimeRepository;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConviveServiceTest {

    @Mock
    private ConviveRepository conviveRepository;

    @Mock
    private SiteRestaurationRepository siteRepository;

    @Mock
    private AllergeneRepository allergeneRepository;

    @Mock
    private RegimeRepository regimeRepository;

    @InjectMocks
    private ConviveService conviveService;

    @Test
    void save_persistsConviveWithSiteAllergenesAndRegimes() {
        SiteRestauration site = new SiteRestauration();
        site.setId(3L);
        site.setNom("Site C");
        site.setType(TypeSite.EHPAD);

        Allergene allergene = new Allergene();
        allergene.setId(5L);
        allergene.setCode("LAIT");
        allergene.setLibelle("Lait");

        Regime regime = new Regime();
        regime.setId(6L);
        regime.setCode("VEGETARIEN");
        regime.setLibelle("Vegetarien");
        regime.setType(RegimeType.ETHIQUE);

        when(siteRepository.findById(3L)).thenReturn(Optional.of(site));
        when(allergeneRepository.findAllById(Set.of(5L))).thenReturn(List.of(allergene));
        when(regimeRepository.findAllById(Set.of(6L))).thenReturn(List.of(regime));
        when(conviveRepository.save(org.mockito.ArgumentMatchers.any(Convive.class)))
            .thenAnswer(invocation -> {
                Convive entity = invocation.getArgument(0);
                entity.setId(12L);
                return entity;
            });

        ConviveRequest request = new ConviveRequest(
            "Martin",
            "Lea",
            TypeConvive.ENFANT_SCOLAIRE,
            3L,
            Set.of(5L),
            Set.of(6L)
        );

        ConviveDto result = conviveService.save(request);

        assertThat(result.id()).isEqualTo(12L);
        assertThat(result.site()).isNotNull();
        assertThat(result.site().id()).isEqualTo(3L);
        assertThat(result.allergenes()).hasSize(1);
        assertThat(result.regimes()).hasSize(1);
    }
}

