package br.com.officyna.administrative.vehicle.domain.mapper;


import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.domain.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity(VehicleRequest request) {
        return Vehicle.builder()
                .customerId(request.customerId())
                .plate(request.plate().toUpperCase())
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .color(request.color())
                .active(true)
                .build();
    }
}