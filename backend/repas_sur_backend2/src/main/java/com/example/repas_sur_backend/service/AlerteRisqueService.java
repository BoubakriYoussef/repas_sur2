package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.AlerteEtatUpdateRequest;
import com.example.repas_sur_backend.dto.AlerteRisqueDto;
import com.example.repas_sur_backend.dto.AlerteRisqueRequest;
import com.example.repas_sur_backend.dto.IdCodeDto;
import com.example.repas_sur_backend.dto.IdNomDto;
import com.example.repas_sur_backend.dto.ServiceRepasDto;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
import com.example.repas_sur_backend.repository.AllergeneRepository;
import com.example.repas_sur_backend.repository.ConviveRepository;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AlerteRisqueService {

    private static final String REGIME_SANS_PORC = "SANS_PORC";
    private static final String REGIME_VEGETARIEN = "VEGETARIEN";

    private final AlerteRisqueRepository alerteRisqueRepository;
    private final ServiceRepasRepository serviceRepasRepository;
    private final ConviveRepository conviveRepository;
    private final AllergeneRepository allergeneRepository;

    public AlerteRisqueService(
        AlerteRisqueRepository alerteRisqueRepository,
        ServiceRepasRepository serviceRepasRepository,
        ConviveRepository conviveRepository,
        AllergeneRepository allergeneRepository
    ) {
        this.alerteRisqueRepository = alerteRisqueRepository;
        this.serviceRepasRepository = serviceRepasRepository;
        this.conviveRepository = conviveRepository;
        this.allergeneRepository = allergeneRepository;
    }

    @Transactional(readOnly = true)
    public List<AlerteRisqueDto> findAll() {
        return alerteRisqueRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AlerteRisqueDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public AlerteRisqueDto save(AlerteRisqueRequest request) {
        AlerteRisque alerte = new AlerteRisque();
        apply(alerte, request);
        return toDto(alerteRisqueRepository.save(alerte));
    }

    public AlerteRisqueDto update(Long id, AlerteRisqueRequest request) {
        AlerteRisque alerte = findEntity(id);
        apply(alerte, request);
        return toDto(alerteRisqueRepository.save(alerte));
    }

    public AlerteRisqueDto updateEtat(Long id, AlerteEtatUpdateRequest request) {
        AlerteRisque alerte = findEntity(id);
        alerte.setEtat(request.etat());
        return toDto(alerteRisqueRepository.save(alerte));
    }

    public void delete(Long id) {
        alerteRisqueRepository.deleteById(id);
    }

    public List<AlerteRisqueDto> genererAlertesPourService(Long serviceId) {
        if (alerteRisqueRepository.existsByServiceId(serviceId)) {
            return List.of();
        }

        ServiceRepas service = serviceRepasRepository.findById(serviceId)
            .orElseThrow(() -> new NotFoundException("ServiceRepas not found: " + serviceId));

        if (service.getMenu() == null || service.getSite() == null) {
            return List.of();
        }

        Set<Plat> plats = service.getMenu().getPlats();
        if (plats == null || plats.isEmpty()) {
            return List.of();
        }

        Set<Allergene> allergenesService = new HashSet<>();
        boolean contientPorc = false;
        boolean tousVegetariens = true;

        for (Plat plat : plats) {
            if (plat.getAllergenes() != null) {
                allergenesService.addAll(plat.getAllergenes());
            }
            if (plat.isContientPorc()) {
                contientPorc = true;
            }
            if (!plat.isEstVegetarien()) {
                tousVegetariens = false;
            }
        }

        List<AlerteRisqueDto> created = new ArrayList<>();
        for (Convive convive : service.getSite().getConvives()) {
            Set<Allergene> allergenesConvive = convive.getAllergenes();
            Set<Allergene> intersection = new HashSet<>();
            if (allergenesConvive != null) {
                intersection.addAll(allergenesConvive);
                intersection.retainAll(allergenesService);
            }

            boolean conflitAllergene = !intersection.isEmpty();
            boolean conflitSansPorc = contientPorc && hasRegime(convive, REGIME_SANS_PORC);
            boolean conflitVegetarien = !tousVegetariens && hasRegime(convive, REGIME_VEGETARIEN);
            boolean conflitRegime = conflitSansPorc || conflitVegetarien;

            if (!conflitAllergene && !conflitRegime) {
                continue;
            }

            AlerteRisque alerte = new AlerteRisque();
            alerte.setConvive(convive);
            alerte.setService(service);
            alerte.setEtat(EtatAlerte.NOUVELLE);
            alerte.setDateCreation(LocalDateTime.now());
            alerte.setNiveau(conflitAllergene ? NiveauAlerte.FORT : NiveauAlerte.MOYEN);
            alerte.setMessage(buildMessage(conflitAllergene, conflitRegime, intersection));

            if (conflitAllergene) {
                alerte.getAllergenes().addAll(intersection);
            }

            created.add(toDto(alerteRisqueRepository.save(alerte)));
        }

        return created;
    }

    private AlerteRisque findEntity(Long id) {
        return alerteRisqueRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("AlerteRisque not found: " + id));
    }

    private void apply(AlerteRisque alerte, AlerteRisqueRequest request) {
        Convive convive = conviveRepository.findById(request.conviveId())
            .orElseThrow(() -> new NotFoundException("Convive not found: " + request.conviveId()));
        ServiceRepas service = serviceRepasRepository.findById(request.serviceId())
            .orElseThrow(() -> new NotFoundException("ServiceRepas not found: " + request.serviceId()));

        alerte.setEtat(request.etat());
        alerte.setNiveau(request.niveau());
        alerte.setMessage(request.message());
        alerte.setDateCreation(request.dateCreation() != null ? request.dateCreation() : LocalDateTime.now());
        alerte.setConvive(convive);
        alerte.setService(service);
        alerte.setAllergenes(fetchAllergenes(request.allergeneIds()));
    }

    private Set<Allergene> fetchAllergenes(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(allergeneRepository.findAllById(ids));
    }

    private boolean hasRegime(Convive convive, String code) {
        return convive.getRegimes().stream()
            .map(Regime::getCode)
            .filter(value -> value != null && !value.isBlank())
            .anyMatch(value -> value.equalsIgnoreCase(code));
    }

    private String buildMessage(boolean conflitAllergene, boolean conflitRegime, Set<Allergene> allergenes) {
        StringBuilder message = new StringBuilder("Conflit detecte pour le service.");
        if (conflitAllergene) {
            String codes = allergenes.stream()
                .map(Allergene::getCode)
                .filter(value -> value != null && !value.isBlank())
                .sorted()
                .collect(Collectors.joining(", "));
            message.append(" Allergene(s): ").append(codes);
        }
        if (conflitRegime) {
            message.append(" Regime incompatible.");
        }
        return message.toString();
    }

    private AlerteRisqueDto toDto(AlerteRisque alerte) {
        Convive convive = alerte.getConvive();
        IdNomDto conviveDto = convive == null
            ? null
            : new IdNomDto(convive.getId(), convive.getNom() + " " + convive.getPrenom());

        ServiceRepas service = alerte.getService();
        ServiceRepasDto serviceDto = null;
        if (service != null) {
            SiteRestauration site = service.getSite();
            SiteRestaurationDto siteDto = site == null
                ? null
                : new SiteRestaurationDto(site.getId(), site.getNom(), site.getType(), site.getAdresse());
            IdNomDto menuDto = service.getMenu() == null
                ? null
                : new IdNomDto(service.getMenu().getId(), service.getMenu().getNom());

            serviceDto = new ServiceRepasDto(
                service.getId(),
                service.getDateService(),
                service.getTypeRepas(),
                service.getStatut(),
                siteDto,
                menuDto
            );
        }

        Set<IdCodeDto> allergenes = alerte.getAllergenes().stream()
            .map(allergene -> new IdCodeDto(allergene.getId(), allergene.getCode(), allergene.getLibelle()))
            .collect(java.util.stream.Collectors.toSet());

        return new AlerteRisqueDto(
            alerte.getId(),
            alerte.getEtat(),
            alerte.getNiveau(),
            alerte.getMessage(),
            alerte.getDateCreation(),
            conviveDto,
            serviceDto,
            allergenes
        );
    }
}
