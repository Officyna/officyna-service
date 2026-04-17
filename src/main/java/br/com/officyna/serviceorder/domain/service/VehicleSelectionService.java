package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleSelectionService {

    private final VehicleService vehicleService;

    public VehicleDTO getVehicle(@NotBlank(message = "ID do Veículo é obrigatório") String id) {
        VehicleResponse response = vehicleService.findById(id);
        return new VehicleDTO(response.id(), response.plate(), response.brand(), response.model(), response.color());
    }

}
