package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.LaborList;
import br.com.officyna.serviceorder.domain.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.SupplyList;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static br.com.officyna.serviceorder.domain.ServiceOrderStatus.RECEBIDA;

@Component
public class ServiceOrderMapper {

    public ServiceOrderEntity toEntity(ServiceOrderRequest request){
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .vehicleId(request.getVehicleId())
                .customerId(request.getCustomerId())
                .informationText(request.getInformationText())
                .laborsList(this.getLaborsList(request.getLaborIds()))
                .supplyList(this.getSupplyLists(request.getSupplyIds()))
                .status(RECEBIDA)
                .build();
        entity.setStatusDate(RECEBIDA);
        return entity;
    }

    private List<LaborList> getLaborsList(List<String> laborsList){
        /*TODO: Implementar construcao da lista de serviços dentro da O.S
        * A O.S recebe a lista de IDs e busca os serviços e vincula os preços ou
        * recebe os serviços e os preços no request?*/
        return null;
    }

    private List<SupplyList> getSupplyLists(List<String> supplyList){
        /*TODO: Implementar construcao da lista de suprimentos dentro da O.S
         *A O.S recebe a lista de IDs e busca os suprimentos e vincula os preços ou
         *recebe os suprimentos e os preços no request?*/
        return null;
    }

    public ServiceOrderResponse toResponse(ServiceOrderEntity entity){

        return ServiceOrderResponse.builder()
                .serviceOrderId(entity.getId())
                .serviceOrderNumber(entity.getServiceOrderNumber().toString())
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicleId())
                .laborList(entity.getLaborsList())
                .supplyList(entity.getSupplyList())
                .informationText(entity.getInformationText())
                .serviceOrderStatus(entity.getStatus().getStatusName())
                .statusDate(this.getStatusDateByLastStatus(entity))
                .totalBudgetAmount(String.format("R$ %.2f", entity.getTotalBudgetAmount()))
                .createdAt(this.formatLocalDateTime(entity.getCreatedAt()))
                .build();
    }

    private String getStatusDateByLastStatus(ServiceOrderEntity entity){
        LocalDateTime statusDate = null;
        switch (entity.getStatus()) {
            case RECEBIDA -> statusDate = entity.getRegistrationDate();
            case EM_DIAGNOSTICO -> statusDate = entity.getDiagnosisDate();
            case AGUARDANDO_APROVACAO -> statusDate = entity.getClientSendDate();
            case APROVADA -> statusDate = entity.getApprovalDate();
            case EM_EXECUCAO -> statusDate = entity.getExecutionStartDate();
            case FINALIZADA, RECUSADA -> statusDate = entity.getFinalizationDate();
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
}
