package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderEntityDocumentMapperTest {

    private ServiceOrderEntityDocumentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ServiceOrderEntityDocumentMapper();
    }

    // --- toDocument tests ---

    @Test
    @DisplayName("Should return null when toDocument receives a null ServiceOrder entity")
    void toDocument_nullEntity_returnsNull() {
        assertNull(mapper.toDocument(null));
    }

    @Test
    @DisplayName("Should correctly map a ServiceOrder entity to ServiceOrderDocument")
    void toDocument_validEntity_returnsDocument() {
        VehicleDTO vehicle = new VehicleDTO("vehicle-id", "plate", "brand", "model", "color");
        CustomerDTO customer = new CustomerDTO();
        MechanicDTO mechanic = new MechanicDTO("mechanic-id", "Mechanic Name");
        LaborsDTO labors = new LaborsDTO(Collections.singletonList(new LaborDetailDTO()), new BigDecimal("100.00"));
        SupplyDTO supplys = new SupplyDTO(Collections.singletonList(new SupplyDetailDTO()), new BigDecimal("50.00"));

        ServiceOrder entity = ServiceOrder.builder()
                .id("so-123")
                    .serviceOrderNumber(1L)
                .vehicle(vehicle)
                .customer(customer)
                .mechanic(mechanic)
                .labors(labors)
                .supplys(supplys)
                .registrationDate(LocalDateTime.now())
                .DiagnosisStartDate(LocalDateTime.now().plusDays(1))
                .clientSendDate(LocalDateTime.now().plusDays(2))
                .approvalDate(LocalDateTime.now().plusDays(3))
                .executionStartDate(LocalDateTime.now().plusDays(4))
                .finalizationDate(LocalDateTime.now().plusDays(5))
                .deliveryDate(LocalDateTime.now().plusDays(6))
                .refuseDate(null)
                .status(ServiceOrderStatus.RECEBIDA)
                .informationText("Some information")
                .totalBudgetAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ServiceOrderDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertEquals(entity.getId(), document.getId());
        assertEquals(entity.getServiceOrderNumber(), document.getServiceOrderNumber());
        assertEquals(entity.getVehicle(), document.getVehicle());
        assertEquals(entity.getCustomer(), document.getCustomer());
        assertEquals(entity.getMechanic(), document.getMechanic());
        assertEquals(entity.getLabors(), document.getLabors());
        assertEquals(entity.getSupplys(), document.getSupplys());
        assertEquals(entity.getRegistrationDate(), document.getRegistrationDate());
        assertEquals(entity.getDiagnosisStartDate(), document.getDiagnosisStartDate());
        assertEquals(entity.getClientSendDate(), document.getClientSendDate());
        assertEquals(entity.getApprovalDate(), document.getApprovalDate());
        assertEquals(entity.getExecutionStartDate(), document.getExecutionStartDate());
        assertEquals(entity.getFinalizationDate(), document.getFinalizationDate());
        assertEquals(entity.getDeliveryDate(), document.getDeliveryDate());
        assertEquals(entity.getRefuseDate(), document.getRefuseDate());
        assertEquals(entity.getStatus().name(), document.getStatus());
        assertEquals(entity.getInformationText(), document.getInformationText());
        assertEquals(entity.getTotalBudgetAmount(), document.getTotalBudgetAmount());
        assertEquals(entity.getCreatedAt(), document.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), document.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a ServiceOrder entity with null status to ServiceOrderDocument")
    void toDocument_entityWithNullStatus_returnsDocumentWithNullStatus() {
        ServiceOrder entity = ServiceOrder.builder()
                .id("so-123")
                .serviceOrderNumber(1L)
                .status(null)
                .build();

        ServiceOrderDocument document = mapper.toDocument(entity);

        assertNotNull(document);
        assertNull(document.getStatus());
    }

    // --- toEntity tests ---

    @Test
    @DisplayName("Should return null when toEntity receives a null ServiceOrderDocument")
    void toEntity_nullDocument_returnsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should correctly map a ServiceOrderDocument to ServiceOrder entity")
    void toEntity_validDocument_returnsEntity() {
        VehicleDTO vehicle = new VehicleDTO();
        CustomerDTO customer = new CustomerDTO();
        MechanicDTO mechanic = new MechanicDTO("mechanic-id", "Mechanic Name");
        LaborsDTO labors = new LaborsDTO(Collections.singletonList(new LaborDetailDTO()), new BigDecimal("100.00"));
        SupplyDTO supplys = new SupplyDTO(Collections.singletonList(new SupplyDetailDTO()), new BigDecimal("50.00"));

        ServiceOrderDocument document = ServiceOrderDocument.builder()
                .id("so-123")
                .serviceOrderNumber(1L)
                .vehicle(vehicle)
                .customer(customer)
                .mechanic(mechanic)
                .labors(labors)
                .supplys(supplys)
                .registrationDate(LocalDateTime.now())
                .DiagnosisStartDate(LocalDateTime.now().plusDays(1))
                .clientSendDate(LocalDateTime.now().plusDays(2))
                .approvalDate(LocalDateTime.now().plusDays(3))
                .executionStartDate(LocalDateTime.now().plusDays(4))
                .finalizationDate(LocalDateTime.now().plusDays(5))
                .deliveryDate(LocalDateTime.now().plusDays(6))
                .refuseDate(null)
                .status(ServiceOrderStatus.RECEBIDA.name())
                .informationText("Some information")
                .totalBudgetAmount(new BigDecimal("150.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ServiceOrder entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertEquals(document.getId(), entity.getId());
        assertEquals(document.getServiceOrderNumber(), entity.getServiceOrderNumber());
        assertEquals(document.getVehicle(), entity.getVehicle());
        assertEquals(document.getCustomer(), entity.getCustomer());
        assertEquals(document.getMechanic(), entity.getMechanic());
        assertEquals(document.getLabors(), entity.getLabors());
        assertEquals(document.getSupplys(), entity.getSupplys());
        assertEquals(document.getRegistrationDate(), entity.getRegistrationDate());
        assertEquals(document.getDiagnosisStartDate(), entity.getDiagnosisStartDate());
        assertEquals(document.getClientSendDate(), entity.getClientSendDate());
        assertEquals(document.getApprovalDate(), entity.getApprovalDate());
        assertEquals(document.getExecutionStartDate(), entity.getExecutionStartDate());
        assertEquals(document.getFinalizationDate(), entity.getFinalizationDate());
        assertEquals(document.getDeliveryDate(), entity.getDeliveryDate());
        assertEquals(document.getRefuseDate(), entity.getRefuseDate());
        assertEquals(ServiceOrderStatus.valueOf(document.getStatus()), entity.getStatus());
        assertEquals(document.getInformationText(), entity.getInformationText());
        assertEquals(document.getTotalBudgetAmount(), entity.getTotalBudgetAmount());
        assertEquals(document.getCreatedAt(), entity.getCreatedAt());
        assertEquals(document.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should correctly map a ServiceOrderDocument with null status string to ServiceOrder entity")
    void toEntity_documentWithNullStatusString_returnsEntityWithNullStatus() {
        ServiceOrderDocument document = ServiceOrderDocument.builder()
                .id("so-123")
                .serviceOrderNumber(1L)
                .status(null)
                .build();

        ServiceOrder entity = mapper.toEntity(document);

        assertNotNull(entity);
        assertNull(entity.getStatus());
    }
}