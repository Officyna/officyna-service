package br.com.officyna.administrative.labor.domain.service;

import br.com.officyna.administrative.labor.api.resources.LaborRequest;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.LaborEntity;
import br.com.officyna.administrative.labor.domain.mapper.LaborMapper;
import br.com.officyna.administrative.labor.repository.LaborRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LaborService {

    private final LaborRepository laborRepository;
    private final LaborMapper laborMapper;
    private final LaborMonitoringService laborMonitoringService;

    public List<LaborResponse> findAll() {
        return laborRepository.findByActiveTrue()
                .stream()
                .map(laborMapper::toResponse)
                .toList();
    }

    public LaborResponse findById(String id) {
        return laborMapper.toResponse(findEntityById(id));
    }

    public LaborResponse create(LaborRequest request) {
        if (laborRepository.existsByName(request.name())) {
            throw new DomainException("Labor already registered with name: " + request.name());
        }
        LaborEntity entity = laborMapper.toEntity(request);
        LaborEntity saved = laborRepository.save(entity);
        laborMonitoringService.initializeFromEstimate(saved.getId(), saved.getName(), saved.getDescription(), saved.getExecutionTimeInDays());
        return laborMapper.toResponse(saved);
    }

    public LaborResponse update(String id, LaborRequest request) {
        LaborEntity entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equalsIgnoreCase(request.name());
        if (nameChanged && laborRepository.existsByName(request.name())) {
            throw new DomainException("Labor already registered with name: " + request.name());
        }

        laborMapper.updateEntity(entity, request);
        return laborMapper.toResponse(laborRepository.save(entity));
    }

    public void delete(String id) {
        LaborEntity entity = findEntityById(id);
        entity.setActive(false);
        laborRepository.save(entity);
    }

    public LaborEntity findEntityById(String id) {
        return laborRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Labor", id));
    }
}
