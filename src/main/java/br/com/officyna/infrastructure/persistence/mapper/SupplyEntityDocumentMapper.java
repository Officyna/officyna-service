package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.supply.domain.SupplyEntity;
import br.com.officyna.administrative.supply.domain.SupplyType;
import br.com.officyna.infrastructure.persistence.mongodb.model.SupplyDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre SupplyEntity (domínio) e SupplyDocument (infraestrutura).
 */
@Component
public class SupplyEntityDocumentMapper {

    public SupplyDocument toDocument(SupplyEntity entity) {
        if (entity == null) {
            return null;
        }
        return SupplyDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .stockQuantity(entity.getStockQuantity())
                .minimumQuantity(entity.getMinimumQuantity())
                .reservedQuantity(entity.getReservedQuantity())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public SupplyEntity toEntity(SupplyDocument document) {
        if (document == null) {
            return null;
        }
        return SupplyEntity.builder()
                .id(document.getId())
                .name(document.getName())
                .description(document.getDescription())
                .type(document.getType() != null ? SupplyType.valueOf(document.getType()) : null)
                .purchasePrice(document.getPurchasePrice())
                .salePrice(document.getSalePrice())
                .stockQuantity(document.getStockQuantity())
                .minimumQuantity(document.getMinimumQuantity())
                .reservedQuantity(document.getReservedQuantity())
                .active(document.getActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

