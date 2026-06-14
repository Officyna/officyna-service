package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.entity.Labor;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.domain.repository.LaborRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import java.util.List;

public class LaborService {

    private final LaborRepository repository;
    private final LaborMapper laborMapper;
    private final LaborMonitoringService laborMonitoringService;

    public LaborService(LaborRepository repository, LaborMapper laborMapper, LaborMonitoringService laborMonitoringService) {
        this.repository = repository;
        this.laborMapper = laborMapper;
        this.laborMonitoringService = laborMonitoringService;
    }

    public List<LaborResponse> findAll() {
        return repository.findByActiveTrue()
                .stream()
                .map(laborMapper::toResponse)
                .toList();
    }

    public LaborResponse findById(String id) {
        return laborMapper.toResponse(findEntityById(id));
    }

    public LaborResponse create(LaborRequest request) {
        if (repository.existsByName(request.name())) {
            throw new DomainException("Labor already registered with name: " + request.name());
        }
        Labor entity = laborMapper.toEntity(request);
        Labor saved = repository.save(entity);
        laborMonitoringService.initializeFromEstimate(saved.getId(), saved.getName(), saved.getDescription(), saved.getExecutionTimeInDays());
        return laborMapper.toResponse(saved);
    }

    public LaborResponse update(String id, LaborRequest request) {
        Labor entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equalsIgnoreCase(request.name());
        if (nameChanged && repository.existsByName(request.name())) {
            throw new DomainException("Labor already registered with name: " + request.name());
        }

        laborMapper.updateEntity(entity, request);
        return laborMapper.toResponse(repository.save(entity));
    }

    public void delete(String id) {
        Labor entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    public Labor findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Labor", id));
    }
}
