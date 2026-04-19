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
}
