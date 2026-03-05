package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.PlatDto;
import com.example.repas_sur_backend.dto.PlatRequest;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.PlatRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlatServiceTest {

    @Mock
    private PlatRepository platRepository;

    @Mock
    private AllergeneRepository allergeneRepository;

    @InjectMocks
    private PlatService platService;

    @Test
    void save_persistsPlatWithAllergenes() {
        Allergene allergene = new Allergene();
        allergene.setId(7L);
        allergene.setCode("GLUTEN");
        allergene.setLibelle("Gluten");

        when(allergeneRepository.findAllById(Set.of(7L))).thenReturn(List.of(allergene));
        when(platRepository.save(org.mockito.ArgumentMatchers.any(Plat.class)))
            .thenAnswer(invocation -> {
                Plat entity = invocation.getArgument(0);
                entity.setId(4L);
                return entity;
            });

        PlatRequest request = new PlatRequest("Gratin", "PLAT", "Desc", false, true, Set.of(7L));
        PlatDto result = platService.save(request);

        assertThat(result.id()).isEqualTo(4L);
        assertThat(result.contientPorc()).isFalse();
        assertThat(result.estVegetarien()).isTrue();
        assertThat(result.allergenes()).hasSize(1);
        assertThat(result.allergenes().iterator().next().code()).isEqualTo("GLUTEN");
    }
}

