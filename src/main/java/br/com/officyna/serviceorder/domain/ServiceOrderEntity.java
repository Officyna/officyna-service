package br.com.officyna.serviceorder.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static lombok.AccessLevel.NONE;

@Document(collection = "service_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderEntity {

    @Id
    private String id;

    private String vehicleId;

    private String customerId;

    private List<LaborList> laborsList;

    private List<SupplyList> supplyList;

    @Setter(NONE)
    @CreatedDate
    private LocalDateTime  registrationDate;

    private LocalDateTime diagnosisDate;

    private LocalDateTime clientSendDate;

    private LocalDateTime approvalDate;

    private LocalDateTime executionStartDate;

    private LocalDateTime finalizationDate;

    private ServiceOrderStatus status;

    private String informationText;

    private String mechanicId;

    private BigDecimal totalBudgetAmount;

    @Setter(NONE)
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
