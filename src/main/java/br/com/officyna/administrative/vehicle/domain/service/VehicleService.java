package br.com.officyna.administrative.vehicle.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.repository.VehicleRepository;
import br.com.officyna.administrative.vehicle.domain.exception.VehicleBusinessException;
import br.com.officyna.administrative.vehicle.domain.exception.VehicleNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VehicleService {

    private final VehicleRepository repository;
    private final CustomerService customerService;

    public VehicleService(
            VehicleRepository repository,
            CustomerService customerService) {
        this.repository = repository;
        this.customerService = customerService;
    }

    public List<Vehicle> findAll() {
        log.info("Finding all vehicles");

        List<Vehicle> vehicles = repository.findAll();

        log.info("Vehicles found: {}", vehicles.size());

        return vehicles;
    }

    public Vehicle findById(String id) {
        log.info("Finding vehicle by id: {}", id);

        Vehicle vehicle = findEntityById(id);

        log.info("Vehicle found by id: {}", id);

        return vehicle;
    }

    public List<Vehicle> findByCustomer(String customerId) {
        log.info("Finding vehicles by customer id: {}", customerId);

        List<Vehicle> vehicles = repository.findByCustomerId(customerId);

        log.info(
                "Vehicles found for customer id {}: {}",
                customerId,
                vehicles.size()
        );

        return vehicles;
    }

    public Vehicle create(Vehicle vehicle) {
        log.info("Creating vehicle with plate: {}", vehicle.getPlate());

        if (repository.existsByPlate(vehicle.getPlate())) {
            log.warn(
                    "Vehicle creation failed. Plate already registered: {}",
                    vehicle.getPlate()
            );

            throw new VehicleBusinessException(
                    "Plate already registered: " + vehicle.getPlate()
            );
        }

        Customer customer = customerService.findEntityById(
                vehicle.getCustomerId()
        );

        vehicle.setCustomerName(customer.getName());

        Vehicle savedVehicle = repository.save(vehicle);

        log.info(
                "Vehicle created successfully with id: {} and plate: {}",
                savedVehicle.getId(),
                savedVehicle.getPlate()
        );

        return savedVehicle;
    }

    public Vehicle update(String id, Vehicle changes) {
        log.info("Updating vehicle with id: {}", id);

        Vehicle entity = findEntityById(id);

        boolean plateChanged = !entity.getPlate().equals(changes.getPlate());

        if (plateChanged && repository.existsByPlate(changes.getPlate())) {
            log.warn(
                    "Vehicle update failed. Plate already registered: {}",
                    changes.getPlate()
            );

            throw new VehicleBusinessException(
                    "Plate already registered: " + changes.getPlate()
            );
        }

        Customer customer = customerService.findEntityById(
                changes.getCustomerId()
        );

        entity.setCustomerId(customer.getId());
        entity.setCustomerName(customer.getName());
        entity.setPlate(changes.getPlate());
        entity.setBrand(changes.getBrand());
        entity.setModel(changes.getModel());
        entity.setYear(changes.getYear());
        entity.setColor(changes.getColor());

        Vehicle updatedVehicle = repository.save(entity);

        log.info(
                "Vehicle updated successfully with id: {}",
                updatedVehicle.getId()
        );

        return updatedVehicle;
    }

    public void delete(String id) {
        log.info("Deleting vehicle with id: {}", id);

        Vehicle entity = findEntityById(id);

        entity.setActive(false);
        repository.save(entity);

        log.info("Vehicle deactivated successfully with id: {}", id);
    }

    // Utility method for internal use (e.g. WorkOrderService)
    public Vehicle findEntityById(String id) {
        log.debug("Searching vehicle entity by id: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Vehicle not found with id: {}", id);
                    return VehicleNotFoundException.of(id);
                });
    }
}