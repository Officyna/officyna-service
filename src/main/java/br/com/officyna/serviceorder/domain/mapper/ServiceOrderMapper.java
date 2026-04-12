package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.LaborList;
import br.com.officyna.serviceorder.domain.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.SupplyList;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceOrderMapper {

    public ServiceOrderEntity toEntity(ServiceOrderRequest request){
        return ServiceOrderEntity.builder()
                .vehicleId(request.getVehicleId())
                .customerId(request.getCustomerId())
                .informationText(request.getInformationText())
                .laborsList(this.getLaborsList(request.getLaborsId()))
                .supplyList(this.getSupplyLists(request.getSupplysId()))
                .status(ServiceOrderStatus.RECEBIDA)
                .build();
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

        return null;
    }
}
