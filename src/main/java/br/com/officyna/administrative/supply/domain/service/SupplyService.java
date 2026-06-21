package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import br.com.officyna.administrative.supply.domain.entity.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.domain.repository.SupplyRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import java.util.List;

public class SupplyService {

    private final SupplyRepository repository;
    private final SupplyMapper supplyMapper;

    public SupplyService(SupplyRepository repository, SupplyMapper supplyMapper) {
        this.repository = repository;
        this.supplyMapper = supplyMapper;
    }

    public List<SupplyResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(supplyMapper::toResponse)
                .toList();
    }

    public List<SupplyResponse> findByType(SupplyType type) {
        return repository.findByActiveTrueAndType(type)
                .stream()
                .map(supplyMapper::toResponse)
                .toList();
    }

    public SupplyResponse findById(String id) {
        return supplyMapper.toResponse(findEntityById(id));
    }

    public SupplyResponse create(SupplyRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DomainException("Supply already registered with name: " + request.name());
        }
        Supply entity = supplyMapper.toEntity(request);
        return supplyMapper.toResponse(repository.save(entity));
    }

    public SupplyResponse update(String id, SupplyRequest request) {
        Supply entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equals(request.name());
        if (nameChanged && repository.existsByName(request.name())) {
            throw new DomainException("Supply already registered with name: " + request.name());
        }

        supplyMapper.updateEntity(entity, request);
        return supplyMapper.toResponse(repository.save(entity));
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