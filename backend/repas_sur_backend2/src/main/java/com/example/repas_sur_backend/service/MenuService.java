package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.Menu;
import com.example.repas_sur_backend.repository.MenuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Menu getById(Long id) {
        return menuRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + id));
    }

    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }

    public Menu update(Long id, Menu menu) {
        getById(id);
        menu.setId(id);
        return menuRepository.save(menu);
    }

    public void delete(Long id) {
        menuRepository.deleteById(id);
    }
}
