package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.administrative.supply.domain.exception.SupplyBusinessException;
import br.com.officyna.administrative.supply.domain.exception.SupplyNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SupplyService {

    private final SupplyRepository repository;

    public SupplyService(SupplyRepository repository) {
        this.repository = repository;
    }

    public List<Supply> findAll() {
        log.info("Finding all active supplies");

        List<Supply> supplies = repository.findByActiveTrue();

        log.info("Active supplies found: {}", supplies.size());

        return supplies;
    }

    public List<Supply> findByType(SupplyType type) {
        log.info("Finding active supplies by type");

        List<Supply> supplies = repository.findByActiveTrueAndType(type);

        log.info("Active supplies found by type: {}", supplies.size());

        return supplies;
    }

    public Supply findById(String id) {
        log.info("Finding supply by id");

        Supply supply = findEntityById(id);

        log.info("Supply found by id");

        return supply;
    }

    public Supply create(Supply supply) {
        log.info("Creating supply");

        if (repository.existsByName(supply.getName())) {
            log.warn("Supply creation failed: name already registered");

            throw new SupplyBusinessException(
                    "Supply already registered with name: " + supply.getName()
            );
        }

        Supply saved = repository.save(supply);

        log.info("Supply saved successfully");

        return saved;
    }

    public Supply update(String id, Supply changes) {
        log.info("Updating supply");

        Supply entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equals(changes.getName());

        if (nameChanged && repository.existsByName(changes.getName())) {
            log.warn("Supply update failed: name already registered");

            throw new SupplyBusinessException(
                    "Supply already registered with name: " + changes.getName()
            );
        }

        entity.setName(changes.getName());
        entity.setDescription(changes.getDescription());
        entity.setType(changes.getType());
        entity.setPurchasePrice(changes.getPurchasePrice());
        entity.setSalePrice(changes.getSalePrice());
        entity.setStockQuantity(changes.getStockQuantity());
        entity.setMinimumQuantity(changes.getMinimumQuantity());
        entity.setReservedQuantity(changes.getReservedQuantity());

        Supply updated = repository.save(entity);

        log.info("Supply updated successfully");

        return updated;
    }

    public void delete(String id) {
        log.info("Deleting supply");

        Supply entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        log.info("Supply deleted successfully");
    }

    public Supply findEntityById(String id) {
        log.info("Searching supply entity by id");

        Supply supply = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Supply not found by id");
                    return SupplyNotFoundException.of(id);
                });

        log.info("Supply entity found");

        return supply;
    }
}