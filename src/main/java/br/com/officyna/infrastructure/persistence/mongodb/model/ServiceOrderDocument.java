package br.com.officyna.infrastructure.persistence.mongodb.model;

import br.com.officyna.serviceorder.domain.dto.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.NONE;

@Document(collection = "service_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderDocument {

    public static final String SEQUENCE_NAME = "service_orders_sequence";

    @Id
    private String id;

    private Long serviceOrderNumber;

    private VehicleDTO vehicle;

    private CustomerDTO customer;

    private MechanicDTO mechanic;

    private LaborsDTO labors;

    private SupplyDTO supplys;

    private LocalDateTime registrationDate;

    private LocalDateTime DiagnosisStartDate;

    private LocalDateTime clientSendDate;

    private LocalDateTime approvalDate;

    private LocalDateTime executionStartDate;

    private LocalDateTime finalizationDate;

    private LocalDateTime deliveryDate;

    private LocalDateTime refuseDate;

    private String status;

    private String informationText;

    private BigDecimal totalBudgetAmount;

    @Setter(NONE)
    @CreatedDate
    private LocalDateTime createdAt;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}

