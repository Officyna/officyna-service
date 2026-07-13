package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.LaborsDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;
import br.com.officyna.serviceorder.domain.dto.VehicleDTO;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import org.springframework.stereotype.Component;

@Component
public class ServiceOrderMapper {

    public ServiceOrder toCreateEntity(NewServiceOrderRequest request, VehicleDTO vehicle, CustomerDTO customer, LaborsDTO labors){
        return ServiceOrder.builder()
                .vehicle(vehicle)
                .customer(customer)
                .informationText(request.getInformationText())
                .labors(labors)
                .build();
    }

    public ServiceOrder toUpdateEntity(ExistServiceOrderRequest request, ServiceOrder entity, MechanicDTO mechanic){
        entity.setInformationText(request.getInformationText());
        entity.setMechanic(mechanic);
        return entity;
    }
}