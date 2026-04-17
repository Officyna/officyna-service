package br.com.officyna.administrative.supply.domain.service;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.administrative.supply.domain.mapper.SupplyMapper;
import br.com.officyna.administrative.supply.repository.SupplyRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyMapper supplyMapper;

    public List<SupplyResponse> findAll() {
        return supplyRepository.findByActiveTrue()
                .stream()
                .map(supplyMapper::toResponse)
                .toList();
    }

    public List<SupplyResponse> findByType(SupplyType type) {
        return supplyRepository.findByActiveTrueAndType(type)
                .stream()
                .map(supplyMapper::toResponse)
                .toList();
    }

    public SupplyResponse findById(String id) {
        return supplyMapper.toResponse(findEntityById(id));
    }

    public SupplyResponse create(SupplyRequest request) {
        if (supplyRepository.existsByName(request.name())) {
            throw new DomainException("Supply already registered with name: " + request.name());
        }
        SupplyEntity entity = supplyMapper.toEntity(request);
        return supplyMapper.toResponse(supplyRepository.save(entity));
    }

    public SupplyResponse update(String id, SupplyRequest request) {
        SupplyEntity entity = findEntityById(id);

        boolean nameChanged = !entity.getName().equals(request.name());
        if (nameChanged && supplyRepository.existsByName(request.name())) {
            throw new DomainException("Supply already registered with name: " + request.name());
        }

        supplyMapper.updateEntity(entity, request);
        return supplyMapper.toResponse(supplyRepository.save(entity));
    }

    public void delete(String id) {
        SupplyEntity entity = findEntityById(id);
        entity.setActive(false);
        supplyRepository.save(entity);
    }

    public SupplyEntity findEntityById(String id) {
        return supplyRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Supply", id));
    }
}