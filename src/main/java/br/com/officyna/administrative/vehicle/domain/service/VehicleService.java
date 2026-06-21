package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.mapper.VehicleMapper;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import java.util.List;

public class VehicleService {

    private final VehicleRepository repository;
    private final VehicleMapper vehicleMapper;
    private final CustomerService customerService;

    public VehicleService(VehicleRepository repository, VehicleMapper vehicleMapper, CustomerService customerService) {
        this.repository = repository;
        this.vehicleMapper = vehicleMapper;
        this.customerService = customerService;
    }

    public List<VehicleResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse findById(String id) {
        return vehicleMapper.toResponse(findEntityById(id));
    }

    public List<VehicleResponse> findByCustomer(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(vehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse create(VehicleRequest request) {
        if (repository.existsByPlate(request.plate().toUpperCase())) {
            throw new DomainException("Plate already registered: " + request.plate());
        }
        Customer customer = customerService.findEntityById(request.customerId());
        Vehicle entity = vehicleMapper.toEntity(request, customer);
        return vehicleMapper.toResponse(repository.save(entity));
    }

    public VehicleResponse update(String id, VehicleRequest request) {
        Vehicle entity = findEntityById(id);

        boolean plateChanged = !entity.getPlate().equals(request.plate().toUpperCase());
        if (plateChanged && repository.existsByPlate(request.plate().toUpperCase())) {
            throw new DomainException("Plate already registered: " + request.plate());
        }

        Customer customer = customerService.findEntityById(request.customerId());
        vehicleMapper.updateEntity(entity, request, customer);
        return vehicleMapper.toResponse(repository.save(entity));
    }

    public void delete(String id) {
        Vehicle entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    // Utility method for internal use (e.g. WorkOrderService)
    public Vehicle findEntityById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Vehicle", id));
    }
}