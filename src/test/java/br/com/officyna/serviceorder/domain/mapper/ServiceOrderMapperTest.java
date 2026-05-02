package br.com.officyna.serviceorder.domain.mapper;

import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOrderMapperTest {

    private final ServiceOrderMapper mapper = new ServiceOrderMapper();

    private ServiceOrderEntity baseEntity(ServiceOrderStatus status) {
        ServiceOrderEntity e = new ServiceOrderEntity();
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

        ServiceOrderEntity entity = mapper.toCreateEntity(request, vehicle, customer, labors);

        assertThat(entity.getVehicle()).isEqualTo(vehicle);
        assertThat(entity.getCustomer()).isEqualTo(customer);
        assertThat(entity.getLabors()).isEqualTo(labors);
        assertThat(entity.getInformationText()).isEqualTo("Troca de óleo");
    }

    // ─────────────── toUpdateEntity ───────────────

    @Test
    @DisplayName("toUpdateEntity deve atualizar informationText e mecânico")
    void toUpdateEntity_ShouldUpdateInfoAndMechanic() {
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Nova observação", "mech-1");
        MechanicDTO mechanic = new MechanicDTO("mech-1", "Carlos");

        ServiceOrderEntity result = mapper.toUpdateEntity(request, entity, mechanic);

        assertThat(result.getInformationText()).isEqualTo("Nova observação");
        assertThat(result.getMechanic()).isEqualTo(mechanic);
    }

    @Test
    @DisplayName("toUpdateEntity deve definir mechanic como null quando não fornecido")
    void toUpdateEntity_ShouldSetMechanicNull_WhenNotProvided() {
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        MechanicDTO previous = new MechanicDTO("mech-1", "Carlos");
        entity.setMechanic(previous);

        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Info", null);

        ServiceOrderEntity result = mapper.toUpdateEntity(request, entity, null);

        assertThat(result.getMechanic()).isNull();
    }

    // ─────────────── toResponse – status dates ───────────────

    @Test
    @DisplayName("Deve retornar a data de registro quando o status for RECEBIDA")
    void toResponse_ShouldReturnRegistrationDate_WhenStatusIsRecebida() {
        LocalDateTime date = LocalDateTime.of(2024, 1, 15, 9, 30);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        entity.setRegistrationDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("15/01/2024 09:30");
    }

    @Test
    @DisplayName("Deve retornar a data de diagnóstico quando o status for EM_DIAGNOSTICO")
    void toResponse_ShouldReturnDiagnosisDate_WhenStatusIsDiagnosis() {
        LocalDateTime now = LocalDateTime.of(2023, 10, 27, 10, 0);
        ServiceOrderEntity entity = ServiceOrderEntity.builder()
                .id("1")
                .serviceOrderNumber(100L)
                .status(ServiceOrderStatus.EM_DIAGNOSTICO)
                .DiagnosisStartDate(now)
                .totalBudgetAmount(BigDecimal.valueOf(150.50))
                .createdAt(now)
                .build();

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("27/10/2023 10:00");
    }

    @Test
    @DisplayName("Deve retornar a data de envio ao cliente quando o status for AGUARDANDO_APROVACAO")
    void toResponse_ShouldReturnClientSendDate_WhenStatusIsAguardandoAprovacao() {
        LocalDateTime date = LocalDateTime.of(2024, 2, 10, 14, 0);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        entity.setClientSendDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("10/02/2024 14:00");
    }

    @Test
    @DisplayName("Deve retornar a data de aprovação quando o status for APROVADA")
    void toResponse_ShouldReturnApprovalDate_WhenStatusIsAprovada() {
        LocalDateTime date = LocalDateTime.of(2024, 3, 5, 11, 15);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.APROVADA);
        entity.setApprovalDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("05/03/2024 11:15");
    }

    @Test
    @DisplayName("Deve retornar a data de início de execução quando o status for EM_EXECUCAO")
    void toResponse_ShouldReturnExecutionStartDate_WhenStatusIsEmExecucao() {
        LocalDateTime date = LocalDateTime.of(2024, 4, 20, 8, 0);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.EM_EXECUCAO);
        entity.setExecutionStartDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("20/04/2024 08:00");
    }

    @Test
    @DisplayName("Deve retornar a data de finalização quando o status for FINALIZADA")
    void toResponse_ShouldReturnFinalizationDate_WhenStatusIsFinalizda() {
        LocalDateTime date = LocalDateTime.of(2024, 5, 1, 17, 30);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.FINALIZADA);
        entity.setFinalizationDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("01/05/2024 17:30");
    }

    @Test
    @DisplayName("Deve retornar a data de entrega quando o status for ENTREGUE")
    void toResponse_ShouldReturnDeliveryDate_WhenStatusIsEntregue() {
        LocalDateTime date = LocalDateTime.of(2024, 5, 3, 10, 0);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.ENTREGUE);
        entity.setDeliveryDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("03/05/2024 10:00");
    }

    @Test
    @DisplayName("Deve retornar a data de recusa quando o status for RECUSADA")
    void toResponse_ShouldReturnRefuseDate_WhenStatusIsRecusada() {
        LocalDateTime date = LocalDateTime.of(2024, 6, 12, 16, 45);
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECUSADA);
        entity.setRefuseDate(date);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isEqualTo("12/06/2024 16:45");
    }

    @Test
    @DisplayName("Deve retornar null para statusDate quando data do status for nula")
    void toResponse_ShouldReturnNullStatusDate_WhenDateIsNull() {
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        entity.setRegistrationDate(null);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.statusDate()).isNull();
    }

    @Test
    @DisplayName("Deve retornar null para createdAt quando for nulo")
    void toResponse_ShouldReturnNullCreatedAt_WhenNull() {
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.createdAt()).isNull();
    }

    // ─────────────── toResponse – money format ───────────────

    @Test
    @DisplayName("Deve formatar o valor monetário corretamente")
    void toResponse_ShouldFormatMoney() {
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setServiceOrderNumber(1L);
        entity.setStatus(ServiceOrderStatus.RECEBIDA);
        entity.setTotalBudgetAmount(new BigDecimal("1234.5"));

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.totalBudgetAmount()).isEqualTo("R$ 1234,50");
    }

    @Test
    @DisplayName("Deve formatar zero corretamente")
    void toResponse_ShouldFormatZeroMoney() {
        ServiceOrderEntity entity = baseEntity(ServiceOrderStatus.RECEBIDA);
        entity.setTotalBudgetAmount(BigDecimal.ZERO);

        ServiceOrderResponse response = mapper.toResponse(entity);

        assertThat(response.totalBudgetAmount()).isEqualTo("R$ 0,00");
    }
}