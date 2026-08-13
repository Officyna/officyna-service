package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.administrative.labor.domain.exception.LaborBusinessException;
import br.com.officyna.administrative.labor.domain.exception.LaborNotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LaborService {

    private static final Logger log = LoggerFactory.getLogger(LaborService.class);

    private final LaborRepository repository;
    private final LaborMonitoringService laborMonitoringService;

    public LaborService(
            LaborRepository repository,
            LaborMonitoringService laborMonitoringService) {
        this.repository = repository;
        this.laborMonitoringService = laborMonitoringService;
    }

    public List<Labor> findAll() {
        log.info("Finding all active labors");

        List<Labor> labors = repository.findByActiveTrue();

        log.info("Active labors found: {}", labors.size());

        return labors;
    }

    public Labor findById(String id) {
        log.info("Finding labor by id");

        Labor labor = findEntityById(id);

        log.info("Labor found by id");

        return labor;
    }

    public Labor create(Labor labor) {
        log.info("Creating labor");

        if (repository.existsByName(labor.getName())) {
            log.warn("Labor creation failed: name already registered");

            throw new LaborBusinessException(
                    "Labor already registered with name: " + labor.getName()
            );
        }

        Labor saved = repository.save(labor);

        log.info("Labor saved successfully");

        laborMonitoringService.initializeFromEstimate(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getExecutionTimeInDays()
        );

        log.info("Labor monitoring initialized successfully");

        return saved;
    }

    public Labor update(String id, Labor changes) {
        log.info("Updating labor");

        Labor entity = findEntityById(id);

        boolean nameChanged =
                !entity.getName().equalsIgnoreCase(changes.getName());

        if (nameChanged && repository.existsByName(changes.getName())) {
            log.warn("Labor update failed: name already registered");

            throw new LaborBusinessException(
                    "Labor already registered with name: " + changes.getName()
            );
        }

        entity.setName(changes.getName());
        entity.setDescription(changes.getDescription());
        entity.setPrice(changes.getPrice());
        entity.setActive(changes.getActive());
        entity.setExecutionTimeInDays(changes.getExecutionTimeInDays());

        Labor updated = repository.save(entity);

        log.info("Labor updated successfully");

        return updated;
    }

    public void delete(String id) {
        log.info("Deleting labor");

        Labor entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        log.info("Labor deleted successfully");
    }

    public Labor findEntityById(String id) {
        log.info("Searching labor entity by id");

        Labor labor = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Labor not found by id");
                    return LaborNotFoundException.of(id);
                });

        log.info("Labor entity found");

        return labor;
    }
}