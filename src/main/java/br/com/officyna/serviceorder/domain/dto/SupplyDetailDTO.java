package br.com.officyna.serviceorder.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyDetailDTO {

    private String id;

    private String name;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    /**
     * Calcula o preço total multiplicando quantidade pelo preço unitário.
     * Se algum dos valores for nulo, retorna ZERO para evitar NullPointerException.
     */

    public void setTotalPrice() {
        if (quantity == null || unitPrice == null) {
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
