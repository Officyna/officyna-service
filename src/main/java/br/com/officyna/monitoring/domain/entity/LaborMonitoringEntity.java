package br.com.officyna.monitoring.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Document(collection = "labor_monitoring")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborMonitoringEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String laborId;

    private String laborName;

    private String laborDescription;

    private Double averageExecutionTimeInDays;

    private Integer totalExecutions;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}