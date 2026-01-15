package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.model.Allergene;
import com.example.repas_sur_backend.model.Convive;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.model.Regime;
import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
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

    public AlerteRisqueService(
        AlerteRisqueRepository alerteRisqueRepository,
        ServiceRepasRepository serviceRepasRepository
    ) {
        this.alerteRisqueRepository = alerteRisqueRepository;
        this.serviceRepasRepository = serviceRepasRepository;
    }

    @Transactional(readOnly = true)
    public List<AlerteRisque> findAll() {
        return alerteRisqueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AlerteRisque getById(Long id) {
        return alerteRisqueRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("AlerteRisque not found: " + id));
    }

    public AlerteRisque save(AlerteRisque alerteRisque) {
        return alerteRisqueRepository.save(alerteRisque);
    }

    public AlerteRisque update(Long id, AlerteRisque alerteRisque) {
        getById(id);
        alerteRisque.setId(id);
        return alerteRisqueRepository.save(alerteRisque);
    }

    public void delete(Long id) {
        alerteRisqueRepository.deleteById(id);
    }

    public List<AlerteRisque> genererAlertesPourService(Long serviceId) {
        ServiceRepas service = serviceRepasRepository.findById(serviceId)
            .orElseThrow(() -> new IllegalArgumentException("ServiceRepas not found: " + serviceId));

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

        List<AlerteRisque> created = new ArrayList<>();
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

            created.add(alerteRisqueRepository.save(alerte));
        }

        return created;
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
}
