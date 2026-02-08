package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.IdNomDto;
import com.example.repas_sur_backend.dto.ServiceRepasDto;
import com.example.repas_sur_backend.dto.ServiceRepasRequest;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.repository.MenuRepository;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceRepasService {

    private final ServiceRepasRepository serviceRepasRepository;
    private final SiteRestaurationRepository siteRepository;
    private final MenuRepository menuRepository;

    public ServiceRepasService(
        ServiceRepasRepository serviceRepasRepository,
        SiteRestaurationRepository siteRepository,
        MenuRepository menuRepository
    ) {
        this.serviceRepasRepository = serviceRepasRepository;
        this.siteRepository = siteRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceRepasDto> findAll() {
        return serviceRepasRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ServiceRepasDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public ServiceRepasDto save(ServiceRepasRequest request) {
        ServiceRepas service = new ServiceRepas();
        apply(service, request);
        return toDto(serviceRepasRepository.save(service));
    }

    public ServiceRepasDto update(Long id, ServiceRepasRequest request) {
        ServiceRepas service = findEntity(id);
        apply(service, request);
        return toDto(serviceRepasRepository.save(service));
    }

    public void delete(Long id) {
        serviceRepasRepository.deleteById(id);
    }

    private ServiceRepas findEntity(Long id) {
        return serviceRepasRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("ServiceRepas not found: " + id));
    }

    private void apply(ServiceRepas service, ServiceRepasRequest request) {
        SiteRestauration site = siteRepository.findById(request.siteId())
            .orElseThrow(() -> new NotFoundException("Site not found: " + request.siteId()));
        Menu menu = null;
        if (request.menuId() != null) {
            menu = menuRepository.findById(request.menuId())
                .orElseThrow(() -> new NotFoundException("Menu not found: " + request.menuId()));
        }

        service.setDateService(request.dateService());
        service.setTypeRepas(request.typeRepas());
        service.setStatut(request.statut());
        service.setSite(site);
        service.setMenu(menu);
    }

    private ServiceRepasDto toDto(ServiceRepas service) {
        SiteRestauration site = service.getSite();
        SiteRestaurationDto siteDto = site == null
            ? null
            : new SiteRestaurationDto(site.getId(), site.getNom(), site.getType(), site.getAdresse());
        Menu menu = service.getMenu();
        IdNomDto menuDto = menu == null ? null : new IdNomDto(menu.getId(), menu.getNom());

        return new ServiceRepasDto(
            service.getId(),
            service.getDateService(),
            service.getTypeRepas(),
            service.getStatut(),
            siteDto,
            menuDto
        );
    }
}
