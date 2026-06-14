package br.com.officyna.administrative.supply.domain;

import br.com.officyna.infrastructure.exception.DomainException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyEntity {

    private String id;

    private String name;

    private String description;

    private SupplyType type;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    private Integer stockQuantity;

    private Integer minimumQuantity;

    private Integer reservedQuantity;

    private Boolean active;

    @Setter(NONE)
    private LocalDateTime createdAt;

    @Setter(NONE)
    private LocalDateTime updatedAt;

    public void setStockQuantity(Integer stockQuantity){
        if(stockQuantity <= 0) throw new DomainException("Stock quantity cannot be negative");
        this.stockQuantity = stockQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity){
        if(minimumQuantity <= 0) throw new DomainException("Minimum quantity cannot be negative");
        this.minimumQuantity = minimumQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity){
        if(reservedQuantity < 0) throw new DomainException("Reserved quantity cannot be negative");
        if(reservedQuantity > this.stockQuantity) throw new DomainException("Estoque insuficiente para o insumo '" + this.getName() +
                "'. Disponível: " + this.stockQuantity +
                ", Solicitado: " + reservedQuantity);
        this.reservedQuantity = reservedQuantity;
    }
}