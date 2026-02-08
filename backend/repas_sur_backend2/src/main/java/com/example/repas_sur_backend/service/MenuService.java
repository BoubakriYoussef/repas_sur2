package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.dto.IdNomDto;
import com.example.repas_sur_backend.dto.MenuDto;
import com.example.repas_sur_backend.dto.MenuRequest;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.model.Plat;
import com.example.repas_sur_backend.repository.MenuRepository;
import com.example.repas_sur_backend.repository.PlatRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final PlatRepository platRepository;

    public MenuService(MenuRepository menuRepository, PlatRepository platRepository) {
        this.menuRepository = menuRepository;
        this.platRepository = platRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuDto> findAll() {
        return menuRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public MenuDto getById(Long id) {
        return toDto(findEntity(id));
    }

    public MenuDto save(MenuRequest request) {
        Menu menu = new Menu();
        apply(menu, request);
        return toDto(menuRepository.save(menu));
    }

    public MenuDto update(Long id, MenuRequest request) {
        Menu menu = findEntity(id);
        apply(menu, request);
        return toDto(menuRepository.save(menu));
    }

    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

    private Menu findEntity(Long id) {
        return menuRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Menu not found: " + id));
    }

    private void apply(Menu menu, MenuRequest request) {
        menu.setNom(request.nom());
        menu.setDescription(request.description());
        menu.setPlats(fetchPlats(request.platIds()));
    }

    private Set<Plat> fetchPlats(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(platRepository.findAllById(ids));
    }

    private MenuDto toDto(Menu menu) {
        Set<IdNomDto> plats = menu.getPlats().stream()
            .map(plat -> new IdNomDto(plat.getId(), plat.getNom()))
            .collect(java.util.stream.Collectors.toSet());

        return new MenuDto(
            menu.getId(),
            menu.getNom(),
            menu.getDescription(),
            plats
        );
    }
}
