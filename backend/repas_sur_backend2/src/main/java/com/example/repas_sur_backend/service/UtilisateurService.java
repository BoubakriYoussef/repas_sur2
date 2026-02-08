package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.UtilisateurDto;
import com.example.repas_sur_backend.dto.UtilisateurRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.SiteRestauration;
import com.example.repas_sur_backend.model.Utilisateur;
import com.example.repas_sur_backend.repository.SiteRestaurationRepository;
import com.example.repas_sur_backend.repository.UtilisateurRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final SiteRestaurationRepository siteRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(
        UtilisateurRepository utilisateurRepository,
        SiteRestaurationRepository siteRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.siteRepository = siteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UtilisateurDto> findAll() {
        return utilisateurRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public UtilisateurDto save(UtilisateurRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        apply(utilisateur, request, true);
        return toDto(utilisateurRepository.save(utilisateur));
    }

    public UtilisateurDto update(Long id, UtilisateurRequest request) {
        Utilisateur utilisateur = findEntity(id);
        apply(utilisateur, request, false);
        return toDto(utilisateurRepository.save(utilisateur));
    }

    public void delete(Long id) {
        utilisateurRepository.deleteById(id);
    }

    private Utilisateur findEntity(Long id) {
        return utilisateurRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Utilisateur not found: " + id));
    }

    private void apply(Utilisateur utilisateur, UtilisateurRequest request, boolean requirePassword) {
        SiteRestauration site = null;
        if (request.siteId() != null) {
            site = siteRepository.findById(request.siteId())
                .orElseThrow(() -> new NotFoundException("Site not found: " + request.siteId()));
        }

        utilisateur.setUsername(request.username());
        if (request.password() != null && !request.password().isBlank()) {
            utilisateur.setPassword(passwordEncoder.encode(request.password()));
        } else if (requirePassword) {
            throw new IllegalArgumentException("Password is required");
        }
        utilisateur.setEmail(request.email());
        utilisateur.setTelephone(request.telephone());
        utilisateur.setPoste(request.poste());
        utilisateur.setRole(request.role());
        utilisateur.setActif(request.actif());
        utilisateur.setSite(site);
    }

    private UtilisateurDto toDto(Utilisateur utilisateur) {
        SiteRestauration site = utilisateur.getSite();
        SiteRestaurationDto siteDto = site == null
            ? null
            : new SiteRestaurationDto(site.getId(), site.getNom(), site.getType(), site.getAdresse());

        return new UtilisateurDto(
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
}
