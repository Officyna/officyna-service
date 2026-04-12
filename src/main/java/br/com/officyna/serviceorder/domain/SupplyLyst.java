package br.com.officyna.serviceorder.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyLyst {

    private String supplyId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
