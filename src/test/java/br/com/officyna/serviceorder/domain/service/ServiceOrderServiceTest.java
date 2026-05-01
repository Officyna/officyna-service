package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    @DisplayName("Deve atualizar status com sucesso quando a precedência for respeitada")
    void updateStatus_ShouldSuccess_WhenTransitionIsValid() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.RECEBIDA);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        // Act
        service.updateStatus(id, ServiceOrderStatus.EM_DIAGNOSTICO);

        // Assert
        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_DIAGNOSTICO);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pular um status na precedência")
    void updateStatus_ShouldThrowException_WhenTransitionIsInvalid() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.RECEBIDA);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.APROVADA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar retornar para o status RECEBIDA")
    void updateStatus_ShouldThrowException_WhenReturningToReceived() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.RECEBIDA))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
    }

    @Test
    @DisplayName("Deve lançar exceção se o novo status for igual ao atual")
    void updateStatus_ShouldThrowException_WhenStatusIsSame() {
        // Arrange
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        // Act & Assert
        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.EM_DIAGNOSTICO))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("A Ordem de Serviço já foi processada com status " + ServiceOrderStatus.EM_DIAGNOSTICO.getStatusName() + ".");
    }

    @Test
    @DisplayName("Deve validar transição para ENTREGUE apenas após FINALIZADA")
    void updateStatus_ShouldValidateDelivery_OnlyAfterFinalized() {
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.updateStatus(id, ServiceOrderStatus.ENTREGUE))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas ordes FINALIZADAS podem ser consideradas entregues");
    }

    @Test
    @DisplayName("findAll deve retornar lista de ServiceOrderResponse")
    void findAll_ShouldReturnListOfResponses() {
        List<ServiceOrderEntity> entities = List.of(new ServiceOrderEntity());
        when(repository.findAll()).thenReturn(entities);
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(any(ServiceOrderEntity.class))).thenReturn(response);

        List<ServiceOrderResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
        verify(mapper).toResponse(any(ServiceOrderEntity.class));
    }

    @Test
    @DisplayName("findById deve retornar ServiceOrderResponse quando encontrado")
    void findById_ShouldReturnResponse_WhenFound() {
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.findById(id);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("findById deve lançar NotFoundException quando não encontrado")
    void findById_ShouldThrowNotFound_WhenNotFound() {
        String id = "123";
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findByServiceOrderNumber deve retornar ServiceOrderResponse quando encontrado")
    void findByServiceOrderNumber_ShouldReturnResponse_WhenFound() {
        Long number = 123L;
        ServiceOrderEntity entity = new ServiceOrderEntity();
        when(repository.findByServiceOrderNumber(number)).thenReturn(Optional.of(entity));
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);
        when(mapper.toResponse(entity)).thenReturn(response);

        ServiceOrderResponse result = service.findByServiceOrderNumber(number);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("findByServiceOrderNumber deve lançar NotFoundException quando não encontrado")
    void findByServiceOrderNumber_ShouldThrowNotFound_WhenNotFound() {
        Long number = 123L;
        when(repository.findByServiceOrderNumber(number)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByServiceOrderNumber(number))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("createServiceOrder deve criar e retornar ServiceOrderResponse")
    void createServiceOrder_ShouldCreateAndReturnResponse() {
        NewServiceOrderRequest request = mock(NewServiceOrderRequest.class);

        LaborsDTO labors = new LaborsDTO();
        CustomerDTO customer = new CustomerDTO();
        VehicleDTO vehicle = new VehicleDTO();
        ServiceOrderEntity entity = new ServiceOrderEntity();
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(laborSelectionService.addLabors(any(), any())).thenReturn(labors);
        when(customerAndMecnichalService.getCustomer(any())).thenReturn(customer);
        when(vehicleSelectionService.getVehicle(any())).thenReturn(vehicle);
        when(mapper.toCreateEntity(request, vehicle, customer, labors)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.createServiceOrder(request);

        assertThat(result).isEqualTo(response);
        verify(statusService).updateStatus(entity, ServiceOrderStatus.RECEBIDA);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("updateServiceOrder deve atualizar e retornar ServiceOrderResponse")
    void updateServiceOrder_ShouldUpdateAndReturnResponse() {
        String id = "123";
        ExistServiceOrderRequest request = mock(ExistServiceOrderRequest.class);

        ServiceOrderEntity entity = new ServiceOrderEntity();
        ServiceOrderEntity updated = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toUpdateEntity(any(), any(), any())).thenReturn(updated);
        when(repository.save(updated)).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(response);

        ServiceOrderResponse result = service.updateServiceOrder(id, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("deleteServiceOrder deve deletar a entidade")
    void deleteServiceOrder_ShouldDeleteEntity() {
        String id = "123";

        service.deleteServiceOrder(id);

        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("addLaborsInServiceOrder deve adicionar labors e retornar response")
    void addLaborsInServiceOrder_ShouldAddLaborsAndReturnResponse() {
        String id = "123";
        List<LaborsRequest> laborsRequests = List.of(mock(LaborsRequest.class));

        ServiceOrderEntity entity = new ServiceOrderEntity();
        LaborsDTO labors = new LaborsDTO();
        entity.setLabors(labors);
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(laborSelectionService.addLabors(laborsRequests, entity.getLabors().getLaborsDetails())).thenReturn(labors);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.addLaborsInServiceOrder(id, laborsRequests);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("removeLaborFromServiceOrder deve remover labor e retornar response")
    void removeLaborFromServiceOrder_ShouldRemoveLaborAndReturnResponse() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId(laborId);
        labors.setLaborsDetails(new ArrayList<>(List.of(labor)));
        entity.setLabors(labors);
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.removeLaborFromServiceOrder(id, laborId);

        assertThat(result).isEqualTo(response);
        assertThat(entity.getLabors().getLaborsDetails()).isEmpty();
    }

    @Test
    @DisplayName("addSupplyFromServiceOrder deve adicionar supplies e retornar response")
    void addSupplyFromServiceOrder_ShouldAddSuppliesAndReturnResponse() {
        String id = "123";
        List<SupplysRequest> supplyRequests = List.of(mock(SupplysRequest.class));

        ServiceOrderEntity entity = new ServiceOrderEntity();
        SupplyDTO supply = new SupplyDTO();
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(supplySelectionService.addSupplys(supplyRequests, List.of())).thenReturn(supply);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.addSupplyFromServiceOrder(id, supplyRequests);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("removeSupplyFromServiceOrder deve remover supply e retornar response")
    void removeSupplyFromServiceOrder_ShouldRemoveSupplyAndReturnResponse() {
        String id = "123";
        String supplyId = "sup-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        SupplyDTO supply = new SupplyDTO();
        entity.setSupplys(supply);
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(supplySelectionService).removeSupply(supply, supplyId);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.removeSupplyFromServiceOrder(id, supplyId);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("startLabor deve iniciar labor e retornar response")
    void startLabor_ShouldStartLaborAndReturnResponse() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.APROVADA);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId(laborId);
        labors.setLaborsDetails(List.of(labor));
        entity.setLabors(labors);
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.startLabor(id, laborId);

        assertThat(result).isEqualTo(response);
        assertThat(labor.getStartDate()).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(ServiceOrderStatus.EM_EXECUCAO);
    }

    @Test
    @DisplayName("startLabor deve lançar DomainException se labor já iniciado")
    void startLabor_ShouldThrowException_IfAlreadyStarted() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.APROVADA);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId(laborId);
        labor.setStartDate(java.time.LocalDateTime.now());
        labors.setLaborsDetails(List.of(labor));
        entity.setLabors(labors);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.startLabor(id, laborId))
                .isInstanceOf(DomainException.class)
                .hasMessage("O serviço já foi iniciado");
    }

    @Test
    @DisplayName("startLabor deve lançar NotFoundException se labor não existir na O.S.")
    void startLabor_ShouldThrowNotFound_IfLaborNotFound() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.APROVADA);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO other = new LaborDetailDTO();
        other.setLaborId("other-lab");
        labors.setLaborsDetails(new ArrayList<>(List.of(other)));
        entity.setLabors(labors);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.startLabor(id, laborId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("A O.S não possui este serviço");
    }

    @Test
    @DisplayName("finishLabor deve finalizar labor e retornar response")
    void finishLabor_ShouldFinishLaborAndReturnResponse() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId(laborId);
        labor.setStartDate(java.time.LocalDateTime.now());
        labors.setLaborsDetails(List.of(labor));
        entity.setLabors(labors);
        ServiceOrderEntity saved = new ServiceOrderEntity();
        ServiceOrderResponse response = mock(ServiceOrderResponse.class);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ServiceOrderResponse result = service.finishLabor(id, laborId);

        assertThat(result).isEqualTo(response);
        assertThat(labor.getEndDate()).isNotNull();
        verify(laborMonitoringService).updateExecutionTimeInDays(eq(laborId), any(), any());
    }

    @Test
    @DisplayName("finishLabor deve lançar DomainException se labor não iniciado")
    void finishLabor_ShouldThrowException_IfNotStarted() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO labor = new LaborDetailDTO();
        labor.setLaborId(laborId);
        labors.setLaborsDetails(List.of(labor));
        entity.setLabors(labors);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.finishLabor(id, laborId))
                .isInstanceOf(DomainException.class)
                .hasMessage("Não é possível finalizar um serviço que não foi iniciado ou já foi finalizado.");
    }

    @Test
    @DisplayName("finishLabor deve lançar NotFoundException se labor não existir na O.S.")
    void finishLabor_ShouldThrowNotFound_IfLaborNotFound() {
        String id = "123";
        String laborId = "lab-1";

        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);
        LaborsDTO labors = new LaborsDTO();
        LaborDetailDTO other = new LaborDetailDTO();
        other.setLaborId("other-lab");
        labors.setLaborsDetails(new ArrayList<>(List.of(other)));
        entity.setLabors(labors);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.finishLabor(id, laborId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("A O.S não possui este serviço");
    }

    @Test
    @DisplayName("save deve calcular budget e salvar")
    void save_ShouldCalculateBudgetAndSave() {
        ServiceOrderEntity entity = new ServiceOrderEntity();

        when(repository.save(entity)).thenReturn(entity);

        ServiceOrderEntity result = service.save(entity);

        assertThat(result).isEqualTo(entity);
        verify(budgetService).calculateBudget(entity);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("sendToCustomer deve atualizar status e salvar")
    void sendToCustomer_ShouldUpdateStatusAndSave() {
        String id = "123";
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setStatus(ServiceOrderStatus.EM_DIAGNOSTICO);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        SendToCustomerResponse result = service.sendToCustomer(id);

        assertThat(result.message()).isEqualTo("Ordem de serviço enviada para o cliente");
        verify(statusService).updateStatus(entity, ServiceOrderStatus.AGUARDANDO_APROVACAO);
        verify(repository).save(entity);
    }
}
