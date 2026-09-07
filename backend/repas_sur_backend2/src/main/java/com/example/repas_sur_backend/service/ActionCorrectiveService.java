package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.ActionCorrectiveDto;
import com.example.repas_sur_backend.dto.ActionCorrectiveRequest;
import com.example.repas_sur_backend.dto.IdNomDto;
import com.example.repas_sur_backend.dto.UtilisateurDto;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.exception.ConflictException;
import com.example.repas_sur_backend.model.ActionCorrective;
import com.example.repas_sur_backend.model.AlerteRisque;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.repository.ActionCorrectiveRepository;
import com.example.repas_sur_backend.repository.AlerteRisqueRepository;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActionCorrectiveService {

    private final ActionCorrectiveRepository actionCorrectiveRepository;
    private final AlerteRisqueRepository alerteRisqueRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ActionCorrectiveService(
        ActionCorrectiveRepository actionCorrectiveRepository,
        AlerteRisqueRepository alerteRisqueRepository,
        UtilisateurRepository utilisateurRepository
    ) {
        this.actionCorrectiveRepository = actionCorrectiveRepository;
        this.alerteRisqueRepository = alerteRisqueRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<ActionCorrectiveDto> findAll() {
        return actionCorrectiveRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ActionCorrectiveDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public ActionCorrectiveDto save(ActionCorrectiveRequest request, String authenticatedUsername) {
        if (actionCorrectiveRepository.existsByAlerteId(request.alerteId())) {
            throw new ConflictException("Une action corrective existe deja pour cette alerte");
        }
        ActionCorrective action = new ActionCorrective();
        apply(action, request);
        Utilisateur creator = utilisateurRepository.findByUsername(authenticatedUsername)
            .orElseThrow(() -> new NotFoundException("Utilisateur authentifie introuvable: " + authenticatedUsername));
        action.setUtilisateur(creator);
        return toDto(actionCorrectiveRepository.save(action));
    }

    public ActionCorrectiveDto update(Long id, ActionCorrectiveRequest request) {
        ActionCorrective action = findEntity(id);
        if (actionCorrectiveRepository.existsByAlerteIdAndIdNot(request.alerteId(), id)) {
            throw new ConflictException("Une action corrective existe deja pour cette alerte");
        }
        apply(action, request);
        return toDto(actionCorrectiveRepository.save(action));
    }

    public void delete(Long id) {
        actionCorrectiveRepository.deleteById(id);
    }

    private ActionCorrective findEntity(Long id) {
        return actionCorrectiveRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("ActionCorrective not found: " + id));
    }

    private void apply(ActionCorrective action, ActionCorrectiveRequest request) {
        AlerteRisque alerte = alerteRisqueRepository.findById(request.alerteId())
            .orElseThrow(() -> new NotFoundException("AlerteRisque not found: " + request.alerteId()));
        action.setDate(request.date());
        action.setTypeAction(request.typeAction());
        action.setDescription(request.description());
        action.setAlerte(alerte);
    }

    private ActionCorrectiveDto toDto(ActionCorrective action) {
        AlerteRisque alerte = action.getAlerte();
        IdNomDto alerteDto = alerte == null ? null : new IdNomDto(alerte.getId(), "Alerte " + alerte.getId());
        IdNomDto conviveDto = null;
        if (alerte != null && alerte.getConvive() != null) {
            String nomComplet = alerte.getConvive().getNom() + " " + alerte.getConvive().getPrenom();
            conviveDto = new IdNomDto(alerte.getConvive().getId(), nomComplet);
        }

        Utilisateur utilisateur = action.getUtilisateur();
        UtilisateurDto utilisateurDto = null;
        if (utilisateur != null) {
            SiteRestauration site = utilisateur.getSite();
            SiteRestaurationDto siteDto = site == null
                ? null
                : new SiteRestaurationDto(site.getId(), site.getNom(), site.getType(), site.getAdresse());
            utilisateurDto = new UtilisateurDto(
                utilisateur.getId(),
                utilisateur.getUsername(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getPoste(),
                utilisateur.getRole(),
                utilisateur.isActif(),
                siteDto
            );
        }

        return new ActionCorrectiveDto(
            action.getId(),
            action.getDate(),
            action.getTypeAction(),
            action.getDescription(),
            alerteDto,
            conviveDto,
            utilisateurDto
        );
    }
}
