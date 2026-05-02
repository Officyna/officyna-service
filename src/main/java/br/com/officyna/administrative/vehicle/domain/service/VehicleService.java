package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.repository.VehicleRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final CustomerService customerService;

    public List<VehicleResponse> findAll() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse findById(String id) {
        return vehicleMapper.toResponse(findEntityById(id));
    }

    public List<VehicleResponse> findByCustomer(String customerId) {
        return vehicleRepository.findByCustomerId(customerId)
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByPlate(request.plate().toUpperCase())) {
            throw new DomainException("Plate already registered: " + request.plate());
        }
        CustomerEntity customer = customerService.findEntityById(request.customerId());
        VehicleEntity entity = vehicleMapper.toEntity(request, customer);
        return vehicleMapper.toResponse(vehicleRepository.save(entity));
    }

    public VehicleResponse update(String id, VehicleRequest request) {
        VehicleEntity entity = findEntityById(id);

        boolean plateChanged = !entity.getPlate().equals(request.plate().toUpperCase());
        if (plateChanged && vehicleRepository.existsByPlate(request.plate().toUpperCase())) {
            throw new DomainException("Plate already registered: " + request.plate());
        }

        CustomerEntity customer = customerService.findEntityById(request.customerId());
        vehicleMapper.updateEntity(entity, request, customer);
        return vehicleMapper.toResponse(vehicleRepository.save(entity));
    }

    public void delete(String id) {
        VehicleEntity entity = findEntityById(id);
        entity.setActive(false);
        vehicleRepository.save(entity);
    }

    // Utility method for internal use (e.g. WorkOrderService)
    public VehicleEntity findEntityById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Vehicle", id));
    }
}