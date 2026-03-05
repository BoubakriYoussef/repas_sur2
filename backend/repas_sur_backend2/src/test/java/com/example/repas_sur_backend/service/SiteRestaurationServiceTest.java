package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.SiteRestaurationRequest;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SiteRestaurationServiceTest {

    @Mock
    private SiteRestaurationRepository siteRepository;

    @InjectMocks
    private SiteRestaurationService siteService;

    @Test
    void findAll_mapsEntitiesToDto() {
        SiteRestauration site = new SiteRestauration();
        site.setId(1L);
        site.setNom("Site A");
        site.setType(TypeSite.SCOLAIRE);
        site.setAdresse("Rue A");

        when(siteRepository.findAll()).thenReturn(List.of(site));

        List<SiteRestaurationDto> result = siteService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nom()).isEqualTo("Site A");
        assertThat(result.get(0).type()).isEqualTo(TypeSite.SCOLAIRE);
    }

    @Test
    void save_persistsSite() {
        SiteRestaurationRequest request = new SiteRestaurationRequest("Site B", TypeSite.EHPAD, "Rue B");
        when(siteRepository.save(org.mockito.ArgumentMatchers.any(SiteRestauration.class)))
            .thenAnswer(invocation -> {
                SiteRestauration entity = invocation.getArgument(0);
                entity.setId(2L);
                return entity;
            });

        SiteRestaurationDto result = siteService.save(request);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.nom()).isEqualTo("Site B");
        assertThat(result.type()).isEqualTo(TypeSite.EHPAD);
    }
}
