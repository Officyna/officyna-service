package br.com.officyna.serviceorder.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyList {

    private String supplyId;

    private Integer supplyQuantity;

    private BigDecimal totalSupplyValue;
}
