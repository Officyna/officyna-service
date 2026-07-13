package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.administrative.labor.domain.exception.LaborBusinessException;
import br.com.officyna.administrative.labor.domain.exception.LaborNotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import java.util.List;

public class LaborService {

    private final LaborRepository repository;
    private final LaborMonitoringService laborMonitoringService;

    public LaborService(LaborRepository repository, LaborMonitoringService laborMonitoringService) {
        this.repository = repository;
        this.laborMonitoringService = laborMonitoringService;
    }

    public List<Labor> findAll() {
        return repository.findByActiveTrue();
    }

    public Labor findById(String id) {
        return findEntityById(id);
    }

    public Labor create(Labor labor) {
        if (repository.existsByName(labor.getName())) {
            throw new LaborBusinessException("Labor already registered with name: " + labor.getName());
        }
        Labor saved = repository.save(labor);
        laborMonitoringService.initializeFromEstimate(saved.getId(), saved.getName(), saved.getDescription(), saved.getExecutionTimeInDays());
        return saved;
    }

    public Labor update(String id, Labor changes) {
        Labor entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equalsIgnoreCase(changes.getName());
        if (nameChanged && repository.existsByName(changes.getName())) {
            throw new LaborBusinessException("Labor already registered with name: " + changes.getName());
        }

        entity.setName(changes.getName());
        entity.setDescription(changes.getDescription());
        entity.setPrice(changes.getPrice());
        entity.setActive(changes.getActive());
        entity.setExecutionTimeInDays(changes.getExecutionTimeInDays());
        return repository.save(entity);
    }

    public void delete(String id) {
        Labor entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    public Labor findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> LaborNotFoundException.of(id));
    }
}