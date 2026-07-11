package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;

public class VehicleSelectionService {

    private final VehicleService vehicleService;

    public VehicleSelectionService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public VehicleDTO getVehicle(String id) {
        Vehicle vehicle = vehicleService.findById(id);
        return new VehicleDTO(vehicle.getId(), vehicle.getPlate(), vehicle.getBrand(), vehicle.getModel(), vehicle.getColor());
    }

}
