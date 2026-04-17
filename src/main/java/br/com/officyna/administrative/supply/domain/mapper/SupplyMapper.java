package br.com.officyna.administrative.supply.domain.mapper;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.SupplyEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SupplyMapper {

    public SupplyEntity toEntity(SupplyRequest request) {
        return SupplyEntity.builder()
                .name(request.name())
                .description(request.description())
                .type(request.type())
                .purchasePrice(request.purchasePrice())
                .salePrice(calculateSalePrice(request.purchasePrice(), request.markupPercentage()))
                .stockQuantity(request.stockQuantity())
                .minimumQuantity(request.minimumQuantity())
                .reservedQuantity(request.reservedQuantity())
                .active(true)
                .build();
    }

    public SupplyResponse toResponse(SupplyEntity entity) {
        int available = entity.getStockQuantity() - entity.getReservedQuantity();
        BigDecimal markup = calculateMarkup(entity.getPurchasePrice(), entity.getSalePrice());
        return new SupplyResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.getPurchasePrice(),
                entity.getSalePrice(),
                markup,
                entity.getStockQuantity(),
                entity.getMinimumQuantity(),
                entity.getReservedQuantity(),
                Math.max(available, 0),
                entity.getStockQuantity() < entity.getMinimumQuantity(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public void updateEntity(SupplyEntity entity, SupplyRequest request) {
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setType(request.type());
        entity.setPurchasePrice(request.purchasePrice());
        entity.setSalePrice(calculateSalePrice(request.purchasePrice(), request.markupPercentage()));
        entity.setStockQuantity(request.stockQuantity());
        entity.setMinimumQuantity(request.minimumQuantity());
        entity.setReservedQuantity(request.reservedQuantity());
    }

    private BigDecimal calculateSalePrice(BigDecimal purchasePrice, BigDecimal markupPercentage) {
        BigDecimal multiplier = BigDecimal.ONE.add(markupPercentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        return purchasePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateMarkup(BigDecimal purchasePrice, BigDecimal salePrice) {
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return salePrice.subtract(purchasePrice)
                .divide(purchasePrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}