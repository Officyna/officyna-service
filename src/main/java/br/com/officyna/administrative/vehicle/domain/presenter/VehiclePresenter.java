package br.com.officyna.administrative.vehicle.domain.presenter;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehiclePresenter {

    public VehicleResponse toResponse(Vehicle entity) {
        return new VehicleResponse(
                entity.getId(),
                entity.getCustomerId(),
                entity.getCustomerName(),
                entity.getPlate(),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getColor(),
                entity.isActive(),
                entity.getCreatedAt()
        );
    }
}