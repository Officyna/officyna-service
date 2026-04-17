package br.com.officyna.serviceorder.domain.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupplyDTO {

    private List<SupplyDetailDTO> supplysDetails;

    private BigDecimal totalSupplyAmount;

    public void calculateTotalSupplyAmount() {
        if (supplysDetails == null || supplysDetails.isEmpty()) {
            this.totalSupplyAmount = BigDecimal.ZERO;
        } else {
            this.totalSupplyAmount = BigDecimal.ZERO;
            for (SupplyDetailDTO item : supplysDetails){
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                this.totalSupplyAmount = this.totalSupplyAmount.add(item.getTotalPrice());
            }
        }
    }
}
