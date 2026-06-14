package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.vehicle.domain.VehicleEntity;
import br.com.officyna.infrastructure.persistence.mongodb.model.VehicleDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre VehicleEntity (domínio) e VehicleDocument (infraestrutura).
 */
@Component
public class VehicleEntityDocumentMapper {

    public VehicleDocument toDocument(VehicleEntity entity) {
        if (entity == null) {
            return null;
        }
        return VehicleDocument.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .plate(entity.getPlate())
                .brand(entity.getBrand())
                .model(entity.getModel())
                .year(entity.getYear())
                .color(entity.getColor())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VehicleEntity toEntity(VehicleDocument document) {
        if (document == null) {
            return null;
        }
        return VehicleEntity.builder()
                .id(document.getId())
                .customerId(document.getCustomerId())
                .customerName(document.getCustomerName())
                .plate(document.getPlate())
                .brand(document.getBrand())
                .model(document.getModel())
                .year(document.getYear())
                .color(document.getColor())
                .active(document.isActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

