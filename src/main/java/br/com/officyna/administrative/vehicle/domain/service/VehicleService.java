package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import java.util.List;

public class VehicleService {

    private final VehicleRepository repository;
    private final CustomerService customerService;

    public VehicleService(VehicleRepository repository, CustomerService customerService) {
        this.repository = repository;
        this.customerService = customerService;
    }

    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    public Vehicle findById(String id) {
        return findEntityById(id);
    }

    public List<Vehicle> findByCustomer(String customerId) {
        return repository.findByCustomerId(customerId);
    }

    public Vehicle create(Vehicle vehicle) {
        if (repository.existsByPlate(vehicle.getPlate())) {
            throw new DomainException("Plate already registered: " + vehicle.getPlate());
        }
        Customer customer = customerService.findEntityById(vehicle.getCustomerId());
        vehicle.setCustomerName(customer.getName());
        return repository.save(vehicle);
    }

    public Vehicle update(String id, Vehicle changes) {
        Vehicle entity = findEntityById(id);

        boolean plateChanged = !entity.getPlate().equals(changes.getPlate());
        if (plateChanged && repository.existsByPlate(changes.getPlate())) {
            throw new DomainException("Plate already registered: " + changes.getPlate());
        }

        Customer customer = customerService.findEntityById(changes.getCustomerId());
        entity.setCustomerId(customer.getId());
        entity.setCustomerName(customer.getName());
        entity.setPlate(changes.getPlate());
        entity.setBrand(changes.getBrand());
        entity.setModel(changes.getModel());
        entity.setYear(changes.getYear());
        entity.setColor(changes.getColor());
        return repository.save(entity);
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