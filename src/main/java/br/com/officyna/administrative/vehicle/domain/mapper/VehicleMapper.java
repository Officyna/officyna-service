package br.com.officyna.administrative.vehicle.domain.mapper;


import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.vehicle.api.resources.VehicleRequest;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleEntity toEntity(VehicleRequest request, CustomerEntity customer) {
        return VehicleEntity.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .plate(request.plate().toUpperCase())
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .color(request.color())
                .active(true)
                .build();
    }

    public VehicleResponse toResponse(VehicleEntity entity) {
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

    public void updateEntity(VehicleEntity entity, VehicleRequest request, CustomerEntity customer) {
        entity.setCustomerId(customer.getId());
        entity.setCustomerName(customer.getName());
        entity.setPlate(request.plate().toUpperCase());
        entity.setBrand(request.brand());
        entity.setModel(request.model());
        entity.setYear(request.year());
        entity.setColor(request.color());
    }
}