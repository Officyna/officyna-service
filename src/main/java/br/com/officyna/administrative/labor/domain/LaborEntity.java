package br.com.officyna.administrative.labor.domain;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Document(collection = "labors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborEntity {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer executionTimeInDays;

    @Setter(NONE)
    @CreatedDate
    private LocalDateTime createdAt;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Boolean active;
}
