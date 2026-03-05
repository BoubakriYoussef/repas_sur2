package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.ServiceRepasDto;
import com.example.repas_sur_backend.dto.ServiceRepasRequest;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.StatutService;
import com.example.repas_sur_backend.model.enums.TypeRepas;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.repository.MenuRepository;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceRepasServiceTest {

    @Mock
    private ServiceRepasRepository serviceRepasRepository;

    @Mock
    private SiteRestaurationRepository siteRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private ServiceRepasService serviceRepasService;

    @Test
    void save_persistsServiceRepasWithSiteAndMenu() {
        SiteRestauration site = new SiteRestauration();
        site.setId(2L);
        site.setNom("Site");
        site.setType(TypeSite.SCOLAIRE);

        Menu menu = new Menu();
        menu.setId(9L);
        menu.setNom("Menu A");

        when(siteRepository.findById(2L)).thenReturn(Optional.of(site));
        when(menuRepository.findById(9L)).thenReturn(Optional.of(menu));
        when(serviceRepasRepository.save(org.mockito.ArgumentMatchers.any(ServiceRepas.class)))
            .thenAnswer(invocation -> {
                ServiceRepas entity = invocation.getArgument(0);
                entity.setId(11L);
                return entity;
            });

        ServiceRepasRequest request = new ServiceRepasRequest(
            LocalDateTime.of(2026, 3, 5, 12, 0),
            TypeRepas.DEJEUNER,
            StatutService.PREVU,
            2L,
            9L
        );

        ServiceRepasDto result = serviceRepasService.save(request);

        assertThat(result.id()).isEqualTo(11L);
        assertThat(result.site()).isNotNull();
        assertThat(result.site().id()).isEqualTo(2L);
        assertThat(result.menu()).isNotNull();
        assertThat(result.menu().id()).isEqualTo(9L);
    }
}

