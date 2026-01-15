package com.example.repas_sur_backend.service;

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
    public List<SiteRestauration> findAll() {
        return siteRestaurationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SiteRestauration getById(Long id) {
        return siteRestaurationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("SiteRestauration not found: " + id));
    }

    public SiteRestauration save(SiteRestauration siteRestauration) {
        return siteRestaurationRepository.save(siteRestauration);
    }

    public SiteRestauration update(Long id, SiteRestauration siteRestauration) {
        getById(id);
        siteRestauration.setId(id);
        return siteRestaurationRepository.save(siteRestauration);
    }

    public void delete(Long id) {
        siteRestaurationRepository.deleteById(id);
    }
}
