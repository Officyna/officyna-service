package br.com.officyna.administrative.supply.domain.presenter;

import br.com.officyna.administrative.supply.api.resources.SupplyResponse;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SupplyPresenter {

    public SupplyResponse toResponse(Supply entity) {
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