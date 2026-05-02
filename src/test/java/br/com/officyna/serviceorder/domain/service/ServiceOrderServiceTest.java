package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository repository;

    @Mock
    private ServiceOrderMapper mapper;

    @Mock
    private BudgetService budgetService;

    @Mock
    private LaborSelectionService laborSelectionService;

    @Mock
    private SupplySelectionService supplySelectionService;

    @Mock
    private CustomerAndMecnichalService customerAndMecnichalService;

    @Mock
    private VehicleSelectionService vehicleSelectionService;

    @Mock
    private LaborMonitoringService laborMonitoringService;

    @Spy
    private StatusService statusService = new StatusService(mock(StockService.class));

    @InjectMocks
    private ServiceOrderService service;

    // ─────────────── helpers ───────────────

    private ServiceOrderEntity buildEntity(String id, ServiceOrderStatus status) {
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setId(id);
        entity.setServiceOrderNumber(1L);
        entity.setStatus(status);
        entity.setTotalBudgetAmount(BigDecimal.ZERO);
        return entity;
    }

    private ServiceOrderResponse buildResponse() {
        return new ServiceOrderResponse("id", "1", null, null, null, null, null, null, "RECEBIDA", null, "R$ 0,00", null);
    }

    // ─────────────── findAll ───────────────

    @Test
    @DisplayName("findAll deve retornar lista de respostas mapeadas")
    void findAll_ShouldReturnMappedList() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ServiceOrderResponse response = buildResponse();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<ServiceOrderResponse> result = service.findAll();

        assertThat(result).hasSize(1).contains(response);
    }

    @Test
    @DisplayName("findAll deve retornar lista vazia quando não há ordens")
    void findAll_ShouldReturnEmptyList_WhenNoOrders() {
        when(repository.findAll()).thenReturn(List.of());

        List<ServiceOrderResponse> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ─────────────── findById ───────────────

    @Test
    @DisplayName("findById deve retornar response quando encontrado")
    void findById_ShouldReturnResponse_WhenFound() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.findById("id-1");

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("findById deve lançar NotFoundException quando não encontrado")
    void findById_ShouldThrowNotFoundException_WhenNotFound() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    // ─────────────── findByServiceOrderNumber ───────────────

    @Test
    @DisplayName("findByServiceOrderNumber deve retornar response quando encontrado")
    void findByServiceOrderNumber_ShouldReturnResponse_WhenFound() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ServiceOrderResponse response = buildResponse();

        when(repository.findByServiceOrderNumber(100L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.findByServiceOrderNumber(100L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("findByServiceOrderNumber deve lançar NotFoundException quando não encontrado")
    void findByServiceOrderNumber_ShouldThrowNotFoundException_WhenNotFound() {
        when(repository.findByServiceOrderNumber(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByServiceOrderNumber(999L))
                .isInstanceOf(NotFoundException.class);
    }

    // ─────────────── createServiceOrder ───────────────

    @Test
    @DisplayName("createServiceOrder deve criar e retornar nova ordem de serviço")
    void createServiceOrder_ShouldCreateAndReturnOrder() {
        NewServiceOrderRequest request = NewServiceOrderRequest.builder()
                .customerId("cust-1")
                .vehicleId("veh-1")
                .laborIds(List.of(new LaborsRequest("lab-1")))
                .informationText("Diagnóstico inicial")
                .build();

        LaborsDTO labors = new LaborsDTO();
        CustomerDTO customer = new CustomerDTO();
        VehicleDTO vehicle = new VehicleDTO();
        ServiceOrderEntity entity = buildEntity(null, null);
        ServiceOrderResponse response = buildResponse();

        when(laborSelectionService.addLabors(any(), any())).thenReturn(labors);
        when(customerAndMecnichalService.getCustomer("cust-1")).thenReturn(customer);
        when(vehicleSelectionService.getVehicle("veh-1")).thenReturn(vehicle);
        when(mapper.toCreateEntity(any(), any(), any(), any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.createServiceOrder(request);

        assertThat(result).isEqualTo(response);
        verify(repository).save(entity);
    }

    // ─────────────── updateServiceOrder ───────────────

    @Test
    @DisplayName("updateServiceOrder deve atualizar com mecânico quando mechanicId fornecido")
    void updateServiceOrder_ShouldUpdateWithMechanic_WhenMechanicIdProvided() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Observação", "mech-1");
        MechanicDTO mechanic = new MechanicDTO("mech-1", "Carlos");
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(customerAndMecnichalService.getMechanic("mech-1")).thenReturn(mechanic);
        when(mapper.toUpdateEntity(request, entity, mechanic)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.updateServiceOrder("id-1", request);

        assertThat(result).isEqualTo(response);
        verify(customerAndMecnichalService).getMechanic("mech-1");
    }

    @Test
    @DisplayName("updateServiceOrder deve atualizar sem mecânico quando mechanicId for nulo")
    void updateServiceOrder_ShouldUpdateWithoutMechanic_WhenMechanicIdIsNull() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ExistServiceOrderRequest request = new ExistServiceOrderRequest("Observação", null);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(mapper.toUpdateEntity(request, entity, null)).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        service.updateServiceOrder("id-1", request);

        verify(customerAndMecnichalService, never()).getMechanic(any());
    }

    // ─────────────── deleteServiceOrder ───────────────

    @Test
    @DisplayName("deleteServiceOrder deve invocar deleteById no repositório")
    void deleteServiceOrder_ShouldCallRepository() {
        doNothing().when(repository).deleteById("id-1");

        service.deleteServiceOrder("id-1");

        verify(repository).deleteById("id-1");
    }

    // ─────────────── addLaborsInServiceOrder ───────────────

    @Test
    @DisplayName("addLaborsInServiceOrder deve adicionar serviços e salvar")
    void addLaborsInServiceOrder_ShouldAddLaborsAndSave() {
        LaborsDTO existingLabors = new LaborsDTO(new ArrayList<>(), BigDecimal.ZERO);
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        entity.setLabors(existingLabors);

        LaborsDTO updatedLabors = new LaborsDTO(List.of(new LaborDetailDTO()), BigDecimal.TEN);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(laborSelectionService.addLabors(any(), any())).thenReturn(updatedLabors);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        ServiceOrderResponse result = service.addLaborsInServiceOrder("id-1", List.of(new LaborsRequest("lab-1")));

        assertThat(result).isEqualTo(response);
        verify(laborSelectionService).addLabors(any(), any());
    }

    // ─────────────── removeLaborFromServiceOrder ───────────────

    @Test
    @DisplayName("removeLaborFromServiceOrder deve remover serviço pelo laborId e salvar")
    void removeLaborFromServiceOrder_ShouldRemoveLaborAndSave() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        entity.setLabors(labors);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        service.removeLaborFromServiceOrder("id-1", "lab-1");

        assertThat(entity.getLabors().getLaborsDetails()).isEmpty();
        verify(repository).save(entity);
    }

    // ─────────────── addSupplyFromServiceOrder ───────────────

    @Test
    @DisplayName("addSupplyFromServiceOrder deve adicionar suprimentos e salvar")
    void addSupplyFromServiceOrder_ShouldAddSuppliesAndSave() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        entity.setSupplys(null);

        SupplyDTO supply = new SupplyDTO(List.of(), BigDecimal.ZERO);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(supplySelectionService.addSupplys(any(), any())).thenReturn(supply);
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        service.addSupplyFromServiceOrder("id-1", List.of(new SupplysRequest("sup-1", 2)));

        verify(supplySelectionService).addSupplys(any(), eq(List.of()));
    }

    // ─────────────── removeSupplyFromServiceOrder ───────────────

    @Test
    @DisplayName("removeSupplyFromServiceOrder deve remover suprimento e salvar")
    void removeSupplyFromServiceOrder_ShouldRemoveSupplyAndSave() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        doNothing().when(supplySelectionService).removeSupply(any(), any());
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        service.removeSupplyFromServiceOrder("id-1", "sup-1");

        verify(supplySelectionService).removeSupply(any(), eq("sup-1"));
        verify(repository).save(entity);
    }

    // ─────────────── updateStatus ───────────────

    @Test
    @DisplayName("Deve atualizar status com sucesso quando a precedência for respeitada")
    void updateStatus_ShouldSuccess_WhenTransitionIsValid() {
        ServiceOrderEntity entity = buildEntity("123", ServiceOrderStatus.RECEBIDA);

        when(repository.findById("123")).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(buildResponse());

        service.updateStatus("123", ServiceOrderStatus.EM_DIAGNOSTICO);

        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pular um status na precedência")
    void updateStatus_ShouldThrowException_WhenTransitionIsInvalid() {
        ServiceOrderEntity entity = buildEntity("123", ServiceOrderStatus.RECEBIDA);

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus("123", ServiceOrderStatus.APROVADA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar retornar para o status RECEBIDA")
    void updateStatus_ShouldThrowException_WhenReturningToReceived() {
        ServiceOrderEntity entity = buildEntity("123", ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus("123", ServiceOrderStatus.RECEBIDA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
    }

    @Test
    @DisplayName("Deve lançar exceção se o novo status for igual ao atual")
    void updateStatus_ShouldThrowException_WhenStatusIsSame() {
        ServiceOrderEntity entity = buildEntity("123", ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus("123", ServiceOrderStatus.EM_DIAGNOSTICO))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi processada com status " + ServiceOrderStatus.EM_DIAGNOSTICO.getStatusName() + ".");
    }

    @Test
    @DisplayName("Deve validar transição para ENTREGUE apenas após FINALIZADA")
    void updateStatus_ShouldValidateDelivery_OnlyAfterFinalized() {
        ServiceOrderEntity entity = buildEntity("123", ServiceOrderStatus.EM_EXECUCAO);

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus("123", ServiceOrderStatus.ENTREGUE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordes FINALIZADAS podem ser consideradas entregues");
    }

    // ─────────────── startLabor ───────────────

    @Test
    @DisplayName("startLabor deve iniciar serviço e mudar status para EM_EXECUCAO quando APROVADA")
    void startLabor_ShouldStart_AndTransitionToEmExecucao() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(null);

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.APROVADA);
        entity.setLabors(labors);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        service.startLabor("id-1", "lab-1");

        assertThat(labor.getStartDate()).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_EXECUCAO);
    }

    @Test
    @DisplayName("startLabor deve lançar DomainException quando serviço já iniciado")
    void startLabor_ShouldThrow_WhenLaborAlreadyStarted() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(LocalDateTime.now());

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.EM_EXECUCAO);
        entity.setLabors(labors);

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.startLabor("id-1", "lab-1"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("O serviço já foi iniciado");
    }

    @Test
    @DisplayName("startLabor deve lançar NotFoundException quando serviço não pertence à OS")
    void startLabor_ShouldThrow_WhenLaborNotFound() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(null);

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.APROVADA);
        entity.setLabors(labors);

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.startLabor("id-1", "lab-inexistente"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("A O.S não possui este serviço");
    }

    @Test
    @DisplayName("startLabor deve lançar DomainException quando status da OS não permite execução")
    void startLabor_ShouldThrow_WhenStatusNotAllowed() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        entity.setLabors(new LaborsDTO(new ArrayList<>(List.of(labor)), BigDecimal.ZERO));

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.startLabor("id-1", "lab-1"))
                .isInstanceOf(DomainException.class);
    }

    // ─────────────── finishLabor ───────────────

    @Test
    @DisplayName("finishLabor deve finalizar serviço e registrar endDate")
    void finishLabor_ShouldFinish_AndRegisterEndDate() {
        LocalDateTime start = LocalDateTime.now().minusHours(4);
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(start);
        labor.setEndDate(null);

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.EM_EXECUCAO);
        entity.setLabors(labors);
        ServiceOrderResponse response = buildResponse();

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        doNothing().when(laborMonitoringService).updateExecutionTimeInDays(any(), any(), any());
        when(repository.save(any())).thenReturn(entity);
        when(mapper.toResponse(any())).thenReturn(response);

        service.finishLabor("id-1", "lab-1");

        assertThat(labor.getEndDate()).isNotNull();
        verify(laborMonitoringService).updateExecutionTimeInDays(eq("lab-1"), eq(start), any());
    }

    @Test
    @DisplayName("finishLabor deve lançar DomainException quando serviço não foi iniciado")
    void finishLabor_ShouldThrow_WhenLaborNotStarted() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(null);
        labor.setEndDate(null);

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.EM_EXECUCAO);
        entity.setLabors(labors);

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.finishLabor("id-1", "lab-1"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Não é possível finalizar um serviço que não foi iniciado ou já foi finalizado.");
    }

    @Test
    @DisplayName("finishLabor deve lançar NotFoundException quando serviço não pertence à OS")
    void finishLabor_ShouldThrow_WhenLaborNotFound() {
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId("lab-1");
        labor.setStartDate(LocalDateTime.now().minusHours(2));
        labor.setEndDate(null);

        List<LaborDetailDTO> details = new ArrayList<>(List.of(labor));
        LaborsDTO labors = new LaborsDTO(details, BigDecimal.TEN);

        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.EM_EXECUCAO);
        entity.setLabors(labors);

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.finishLabor("id-1", "lab-inexistente"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("A O.S não possui este serviço");
    }

    // ─────────────── sendToCustomer ───────────────

    @Test
    @DisplayName("sendToCustomer deve mudar status para AGUARDANDO_APROVACAO e salvar")
    void sendToCustomer_ShouldTransitionToAguardandoAprovacao() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById("id-1")).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        SendToCustomerResponse result = service.sendToCustomer("id-1");

        assertThat(result.message()).contains("enviada para o cliente");
        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        verify(repository).save(entity);
    }

    // ─────────────── save ───────────────

    @Test
    @DisplayName("save deve calcular orçamento e persistir entidade")
    void save_ShouldCalculateBudgetAndPersist() {
        ServiceOrderEntity entity = buildEntity("id-1", ServiceOrderStatus.RECEBIDA);

        when(repository.save(entity)).thenReturn(entity);

        ServiceOrderEntity result = service.save(entity);

        assertThat(result).isEqualTo(entity);
        verify(budgetService).calculateBudget(entity);
        verify(repository).save(entity);
    }
}