package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ServiceOrderMapper {

    public ServiceOrderEntity toCreateEntity(NewServiceOrderRequest request, VehicleDTO vehicle, CustomerDTO customer, LaborsDTO labors){
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .vehicle(vehicle)
                .customer(customer)
                .informationText(request.getInformationText())
                .labors(labors)
                .build();
        return entity;
    }

    public ServiceOrderEntity toUpdateEntity(ExistServiceOrderRequest request, ServiceOrderEntity entity, MechanicDTO mechanic){
        entity.setInformationText(request.getInformationText());
        entity.setMechanic(mechanic);
        return entity;
    }

    public ServiceOrderResponse toResponse(ServiceOrderEntity entity){
        return new ServiceOrderResponse(
                entity.getId(),
                entity.getServiceOrderNumber().toString(),
                entity.getCustomer(),
                entity.getMechanic(),
                entity.getVehicle(),
                entity.getLabors(),
                entity.getSupplys(),
                entity.getInformationText(),
                entity.getStatus().getStatusName(),
                this.getStatusDateByLastStatus(entity),
                this.formatMoney(entity.getTotalBudgetAmount()),
                this.formatLocalDateTime(entity.getCreatedAt())
        );
    }

    private String getStatusDateByLastStatus(ServiceOrderEntity entity){
        LocalDateTime statusDate = null;
        switch (entity.getStatus()) {
            case RECEBIDA -> statusDate = entity.getRegistrationDate();
            case EM_DIAGNOSTICO -> statusDate = entity.getDiagnosisStartDate();
            case AGUARDANDO_APROVACAO -> statusDate = entity.getClientSendDate();
            case APROVADA -> statusDate = entity.getApprovalDate();
            case EM_EXECUCAO -> statusDate = entity.getExecutionStartDate();
            case ENTREGUE -> statusDate = entity.getDeliveryDate();
            case FINALIZADA -> statusDate = entity.getFinalizationDate();
            case RECUSADA -> statusDate = entity.getRefuseDate();
        }
        return this.formatLocalDateTime(statusDate);
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private String formatMoney(BigDecimal value){
        return String.format("R$ %.2f", value);
    }

}
