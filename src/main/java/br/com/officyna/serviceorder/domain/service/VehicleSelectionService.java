package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;

public class VehicleSelectionService {

    private final VehicleService vehicleService;

    public VehicleSelectionService(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    public VehicleDTO getVehicle(String id) {
        VehicleResponse response = vehicleService.findById(id);
        return new VehicleDTO(response.id(), response.plate(), response.brand(), response.model(), response.color());
    }

}
