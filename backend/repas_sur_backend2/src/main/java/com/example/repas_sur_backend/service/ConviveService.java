package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.ConviveDto;
import com.example.repas_sur_backend.dto.ConviveRequest;
import com.example.repas_sur_backend.dto.IdCodeDto;
import com.example.repas_sur_backend.dto.RegimeDto;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.ConviveRepository;
import com.example.repas_sur_backend.repository.RegimeRepository;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConviveService {

    private final ConviveRepository conviveRepository;
    private final SiteRestaurationRepository siteRepository;
    private final AllergeneRepository allergeneRepository;
    private final RegimeRepository regimeRepository;

    public ConviveService(
        ConviveRepository conviveRepository,
        SiteRestaurationRepository siteRepository,
        AllergeneRepository allergeneRepository,
        RegimeRepository regimeRepository
    ) {
        this.conviveRepository = conviveRepository;
        this.siteRepository = siteRepository;
        this.allergeneRepository = allergeneRepository;
        this.regimeRepository = regimeRepository;
    }

    @Transactional(readOnly = true)
    public List<ConviveDto> findAll() {
        return conviveRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ConviveDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public ConviveDto save(ConviveRequest request) {
        Convive convive = new Convive();
        apply(convive, request);
        return toDto(conviveRepository.save(convive));
    }

    public ConviveDto update(Long id, ConviveRequest request) {
        Convive convive = findEntity(id);
        apply(convive, request);
        return toDto(conviveRepository.save(convive));
    }

    public void delete(Long id) {
        conviveRepository.deleteById(id);
    }

    private Convive findEntity(Long id) {
        return conviveRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Convive not found: " + id));
    }

    private void apply(Convive convive, ConviveRequest request) {
        SiteRestauration site = siteRepository.findById(request.siteId())
            .orElseThrow(() -> new NotFoundException("Site not found: " + request.siteId()));

        convive.setNom(request.nom());
        convive.setPrenom(request.prenom());
        convive.setTypeConvive(request.typeConvive());
        convive.setSite(site);
        convive.setAllergenes(fetchAllergenes(request.allergeneIds()));
        convive.setRegimes(fetchRegimes(request.regimeIds()));
    }

    private Set<Allergene> fetchAllergenes(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(allergeneRepository.findAllById(ids));
    }

    private Set<Regime> fetchRegimes(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(regimeRepository.findAllById(ids));
    }

    private ConviveDto toDto(Convive convive) {
        SiteRestauration site = convive.getSite();
        SiteRestaurationDto siteDto = site == null
            ? null
            : new SiteRestaurationDto(site.getId(), site.getNom(), site.getType(), site.getAdresse());

        Set<IdCodeDto> allergenes = convive.getAllergenes().stream()
            .map(allergene -> new IdCodeDto(allergene.getId(), allergene.getCode(), allergene.getLibelle()))
            .collect(java.util.stream.Collectors.toSet());

        Set<RegimeDto> regimes = convive.getRegimes().stream()
            .map(regime -> new RegimeDto(regime.getId(), regime.getCode(), regime.getLibelle(), regime.getType(), regime.getDescription()))
            .collect(java.util.stream.Collectors.toSet());

        return new ConviveDto(
            convive.getId(),
            convive.getNom(),
            convive.getPrenom(),
            convive.getTypeConvive(),
            siteDto,
            allergenes,
            regimes
        );
    }
}
