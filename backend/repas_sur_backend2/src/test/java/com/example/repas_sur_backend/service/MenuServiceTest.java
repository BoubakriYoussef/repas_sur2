package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.MenuDto;
import com.example.repas_sur_backend.dto.MenuRequest;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.repository.MenuRepository;
import com.example.repas_sur_backend.repository.PlatRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private PlatRepository platRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    void save_persistsMenuWithPlats() {
        Plat plat = new Plat();
        plat.setId(10L);
        plat.setNom("Salade");

        when(platRepository.findAllById(Set.of(10L))).thenReturn(List.of(plat));
        when(menuRepository.save(org.mockito.ArgumentMatchers.any(Menu.class)))
            .thenAnswer(invocation -> {
                Menu entity = invocation.getArgument(0);
                entity.setId(3L);
                return entity;
            });

        MenuRequest request = new MenuRequest("Menu midi", "Description", Set.of(10L));
        MenuDto result = menuService.save(request);

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.nom()).isEqualTo("Menu midi");
        assertThat(result.plats()).hasSize(1);
        assertThat(result.plats().iterator().next().id()).isEqualTo(10L);
    }
}

