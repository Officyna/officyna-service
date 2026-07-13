package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMapperTest {

    private final ServiceOrderMapper mapper = new ServiceOrderMapper();

    private ServiceOrder baseEntity(ServiceOrderStatus status) {
        ServiceOrder e = new ServiceOrder();
        e.setServiceOrderNumber(1L);
        e.setStatus(status);
        e.setTotalBudgetAmount(BigDecimal.ZERO);
        return e;
    }

    // ─────────────── toCreateEntity ───────────────

    @Test
    @DisplayName("toCreateEntity deve mapear vehicle, customer, labors e informationText")
    void toCreateEntity_ShouldMapAllFields() {
        NewServiceOrderRequest request = NewServiceOrderRequest.builder()
                .customerId("cust-1")
                .vehicleId("veh-1")
                .informationText("Troca de óleo")
                .laborIds(List.of())
                .build();

        VehicleDTO vehicle = new VehicleDTO();
        CustomerDTO customer = new CustomerDTO();
        LaborsDTO labors = new LaborsDTO();

        ServiceOrder entity = mapper.toCreateEntity(request, vehicle, customer, labors);

        assertThat(entity.getVehicle()).isEqualTo(vehicle);
        assertThat(entity.getCustomer()).isEqualTo(customer);
        assertThat(entity.getLabors()).isEqualTo(labors);
        assertThat(entity.getInformationText()).isEqualTo("Troca de óleo");
    }

    // ─────────────── toUpdateEntity ───────────────

    @Test
    @DisplayName("toUpdateEntity deve atualizar informationText e mecânico")
    void toUpdateEntity_ShouldUpdateInfoAndMechanic() {
        ServiceOrder entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Nova observação", "mech-1");
        MechanicDTO mechanic = new MechanicDTO("mech-1", "Carlos");

        ServiceOrder result = mapper.toUpdateEntity(request, entity, mechanic);

        assertThat(result.getInformationText()).isEqualTo("Nova observação");
        assertThat(result.getMechanic()).isEqualTo(mechanic);
    }

    @Test
    @DisplayName("toUpdateEntity deve definir mechanic como null quando não fornecido")
    void toUpdateEntity_ShouldSetMechanicNull_WhenNotProvided() {
        ServiceOrder entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        MechanicDTO previous = new MechanicDTO("mech-1", "Carlos");
        entity.setMechanic(previous);

        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Info", null);

        ServiceOrder result = mapper.toUpdateEntity(request, entity, null);

        assertThat(result.getMechanic()).isNull();
    }
}