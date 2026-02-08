package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.SiteRestaurationRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SiteRestaurationService {

    private final SiteRestaurationRepository siteRestaurationRepository;

    public SiteRestaurationService(SiteRestaurationRepository siteRestaurationRepository) {
        this.siteRestaurationRepository = siteRestaurationRepository;
    }

    @Transactional(readOnly = true)
    public List<SiteRestaurationDto> findAll() {
        return siteRestaurationRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SiteRestaurationDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public SiteRestaurationDto save(SiteRestaurationRequest request) {
        SiteRestauration site = new SiteRestauration();
        apply(site, request);
        return toDto(siteRestaurationRepository.save(site));
    }

    public SiteRestaurationDto update(Long id, SiteRestaurationRequest request) {
        SiteRestauration site = findEntity(id);
        apply(site, request);
        return toDto(siteRestaurationRepository.save(site));
    }

    public void delete(Long id) {
        siteRestaurationRepository.deleteById(id);
    }

    private SiteRestauration findEntity(Long id) {
        return siteRestaurationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Site not found: " + id));
    }

    private void apply(SiteRestauration site, SiteRestaurationRequest request) {
        site.setNom(request.nom());
        site.setType(request.type());
        site.setAdresse(request.adresse());
    }

    private SiteRestaurationDto toDto(SiteRestauration site) {
        return new SiteRestaurationDto(
            site.getId(),
            site.getNom(),
            site.getType(),
            site.getAdresse()
        );
    }
}
