package br.com.officyna.administrative.supply.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Document(collection = "supplies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyEntity {

    @Id
    private String id;

    private String name;

    private String description;

    private SupplyType type;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    private Integer stockQuantity;

    private Integer minimumQuantity;

    private Integer reservedQuantity;

    private Boolean active;

    @Setter(NONE)
    @CreatedDate
    private LocalDateTime createdAt;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}