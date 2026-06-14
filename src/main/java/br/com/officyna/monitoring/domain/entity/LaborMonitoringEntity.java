package br.com.officyna.monitoring.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborMonitoringEntity {

    private String id;

    private String laborId;

    private String laborName;

    private String laborDescription;

    private Double averageExecutionTimeInDays;

    private Integer totalExecutions;

    @Setter(NONE)
    private LocalDateTime updatedAt;
}