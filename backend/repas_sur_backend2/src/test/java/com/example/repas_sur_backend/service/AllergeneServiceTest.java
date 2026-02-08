package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.AllergeneDto;
import com.example.repas_sur_backend.dto.AllergeneRequest;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllergeneServiceTest {

    @Mock
    private AllergeneRepository allergeneRepository;

    @InjectMocks
    private AllergeneService allergeneService;

    @Test
    void findAll_mapsEntitiesToDto() {
        Allergene allergene = new Allergene();
        allergene.setId(1L);
        allergene.setCode("GLUTEN");
        allergene.setLibelle("Gluten");
        allergene.setDescription("Cereales");

        when(allergeneRepository.findAll()).thenReturn(List.of(allergene));

        List<AllergeneDto> result = allergeneService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo("GLUTEN");
        assertThat(result.get(0).libelle()).isEqualTo("Gluten");
    }

    @Test
    void save_persistsAllergene() {
        AllergeneRequest request = new AllergeneRequest("LAIT", "Lait", "Dairy");
        Allergene saved = new Allergene();
        saved.setId(2L);
        saved.setCode("LAIT");
        saved.setLibelle("Lait");
        saved.setDescription("Dairy");

        when(allergeneRepository.save(org.mockito.ArgumentMatchers.any(Allergene.class))).thenReturn(saved);

        AllergeneDto result = allergeneService.save(request);

        assertThat(result.id()).isEqualTo(2L);
        verify(allergeneRepository).save(org.mockito.ArgumentMatchers.any(Allergene.class));
    }
}
