package br.com.officyna.serviceorder.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborList {

    private String laborId;

    private BigDecimal laborValue;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
