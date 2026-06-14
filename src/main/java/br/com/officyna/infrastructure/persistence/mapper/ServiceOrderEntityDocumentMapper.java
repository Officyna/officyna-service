package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.infrastructure.persistence.mongodb.model.ServiceOrderDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre ServiceOrderEntity (domínio) e ServiceOrderDocument (infraestrutura).
 */
@Component
public class ServiceOrderEntityDocumentMapper {

    public ServiceOrderDocument toDocument(ServiceOrder entity) {
        if (entity == null) {
            return null;
        }
        return ServiceOrderDocument.builder()
                .id(entity.getId())
                .serviceOrderNumber(entity.getServiceOrderNumber())
                .vehicle(entity.getVehicle())
                .customer(entity.getCustomer())
                .mechanic(entity.getMechanic())
                .labors(entity.getLabors())
                .supplys(entity.getSupplys())
                .registrationDate(entity.getRegistrationDate())
                .DiagnosisStartDate(entity.getDiagnosisStartDate())
                .clientSendDate(entity.getClientSendDate())
                .approvalDate(entity.getApprovalDate())
                .executionStartDate(entity.getExecutionStartDate())
                .finalizationDate(entity.getFinalizationDate())
                .deliveryDate(entity.getDeliveryDate())
                .refuseDate(entity.getRefuseDate())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .informationText(entity.getInformationText())
                .totalBudgetAmount(entity.getTotalBudgetAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ServiceOrder toEntity(ServiceOrderDocument document) {
        if (document == null) {
            return null;
        }
        return ServiceOrder.builder()
                .id(document.getId())
                .serviceOrderNumber(document.getServiceOrderNumber())
                .vehicle((br.com.officyna.serviceorder.domain.dto.VehicleDTO) document.getVehicle())
                .customer((br.com.officyna.serviceorder.domain.dto.CustomerDTO) document.getCustomer())
                .mechanic((br.com.officyna.serviceorder.domain.dto.MechanicDTO) document.getMechanic())
                .labors((br.com.officyna.serviceorder.domain.dto.LaborsDTO) document.getLabors())
                .supplys((br.com.officyna.serviceorder.domain.dto.SupplyDTO) document.getSupplys())
                .registrationDate(document.getRegistrationDate())
                .DiagnosisStartDate(document.getDiagnosisStartDate())
                .clientSendDate(document.getClientSendDate())
                .approvalDate(document.getApprovalDate())
                .executionStartDate(document.getExecutionStartDate())
                .finalizationDate(document.getFinalizationDate())
                .deliveryDate(document.getDeliveryDate())
                .refuseDate(document.getRefuseDate())
                .status(document.getStatus() != null ? br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus.valueOf(document.getStatus()) : null)
                .informationText(document.getInformationText())
                .totalBudgetAmount(document.getTotalBudgetAmount())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

