package com.example.repas_sur_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.repas_sur_backend.dto.RegimeDto;
import com.example.repas_sur_backend.dto.RegimeRequest;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.enums.RegimeType;
import com.example.repas_sur_backend.repository.RegimeRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegimeServiceTest {

    @Mock
    private RegimeRepository regimeRepository;

    @InjectMocks
    private RegimeService regimeService;

    @Test
    void findAll_mapsEntitiesToDto() {
        Regime regime = new Regime();
        regime.setId(1L);
        regime.setCode("SANS_PORC");
        regime.setLibelle("Sans porc");
        regime.setType(RegimeType.RELIGIEUX);

        when(regimeRepository.findAll()).thenReturn(List.of(regime));

        List<RegimeDto> result = regimeService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo("SANS_PORC");
        assertThat(result.get(0).type()).isEqualTo(RegimeType.RELIGIEUX);
    }

    @Test
    void save_persistsRegime() {
        RegimeRequest request = new RegimeRequest("VEGETARIEN", "Vegetarien", RegimeType.ETHIQUE, "Sans viande");
        when(regimeRepository.save(org.mockito.ArgumentMatchers.any(Regime.class)))
            .thenAnswer(invocation -> {
                Regime entity = invocation.getArgument(0);
                entity.setId(5L);
                return entity;
            });

        RegimeDto result = regimeService.save(request);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.code()).isEqualTo("VEGETARIEN");
        assertThat(result.type()).isEqualTo(RegimeType.ETHIQUE);
    }
}

