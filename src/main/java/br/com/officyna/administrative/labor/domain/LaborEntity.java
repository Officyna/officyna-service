package br.com.officyna.administrative.labor.domain;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborEntity {

    private String id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer executionTimeInDays;

    @Setter(NONE)
    private LocalDateTime createdAt;

    @Setter(NONE)
    private LocalDateTime updatedAt;

    private Boolean active;
}
