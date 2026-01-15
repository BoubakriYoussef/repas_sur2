package com.example.repas_sur_backend.service;

import com.example.repas_sur_backend.model.ServiceRepas;
import com.example.repas_sur_backend.repository.ServiceRepasRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceRepasService {

    private final ServiceRepasRepository serviceRepasRepository;

    public ServiceRepasService(ServiceRepasRepository serviceRepasRepository) {
        this.serviceRepasRepository = serviceRepasRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceRepas> findAll() {
        return serviceRepasRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ServiceRepas getById(Long id) {
        return serviceRepasRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ServiceRepas not found: " + id));
    }

    public ServiceRepas save(ServiceRepas serviceRepas) {
        return serviceRepasRepository.save(serviceRepas);
    }

    public ServiceRepas update(Long id, ServiceRepas serviceRepas) {
        getById(id);
        serviceRepas.setId(id);
        return serviceRepasRepository.save(serviceRepas);
    }

    public void delete(Long id) {
        serviceRepasRepository.deleteById(id);
    }
}
