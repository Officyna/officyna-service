package br.com.officyna.serviceorder.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LaborsDTO {

    private List<LaborDetailDTO> laborsDetails;

    private BigDecimal totalLaborsAmount;

    public void calculateTotalLaborsAmount(){
        if(this.laborsDetails == null || this.laborsDetails.isEmpty()){
            this.totalLaborsAmount = BigDecimal.ZERO;
            return;
        }
        this.totalLaborsAmount = this.laborsDetails.stream()
                .map(LaborDetailDTO::getLaborPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
