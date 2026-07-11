package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import java.util.List;

public class SupplyService {

    private final SupplyRepository repository;

    public SupplyService(SupplyRepository repository) {
        this.repository = repository;
    }

    public List<Supply> findAll() {
        return repository.findByActiveTrue();
    }

    public List<Supply> findByType(SupplyType type) {
        return repository.findByActiveTrueAndType(type);
    }

    public Supply findById(String id) {
        return findEntityById(id);
    }

    public Supply create(Supply supply) {
        if (repository.existsByName(supply.getName())) {
            throw new DomainException("Supply already registered with name: " + supply.getName());
        }
        return repository.save(supply);
    }

    public Supply update(String id, Supply changes) {
        Supply entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equals(changes.getName());
        if (nameChanged && repository.existsByName(changes.getName())) {
            throw new DomainException("Supply already registered with name: " + changes.getName());
        }

        entity.setName(changes.getName());
        entity.setDescription(changes.getDescription());
        entity.setType(changes.getType());
        entity.setPurchasePrice(changes.getPurchasePrice());
        entity.setSalePrice(changes.getSalePrice());
        entity.setStockQuantity(changes.getStockQuantity());
        entity.setMinimumQuantity(changes.getMinimumQuantity());
        entity.setReservedQuantity(changes.getReservedQuantity());
        return repository.save(entity);
    }

    public void delete(String id) {
        Supply entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    public Supply findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Supply", id));
    }
}
