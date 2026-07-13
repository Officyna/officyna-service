package br.com.officyna.administrative.supply.domain.mapper;

import br.com.officyna.administrative.supply.api.resources.SupplyRequest;
import br.com.officyna.administrative.supply.domain.entity.Supply;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SupplyMapper {

    public Supply toEntity(SupplyRequest request) {
        return Supply.builder()
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

    private BigDecimal calculateSalePrice(BigDecimal purchasePrice, BigDecimal markupPercentage) {
        BigDecimal multiplier = BigDecimal.ONE.add(markupPercentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
        return purchasePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
