package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository repository;

    private final LaborSelectionService laborSelectionService;

    private final SupplySelectionService supplySelectionService;

    private final CustomerAndMecnichalService customerAndMecnichalService;

    private final VehicleSelectionService vehicleSelectionService;

    private final ServiceOrderMapper mapper;

    private final StatusService statusService;

    private final LaborMonitoringService laborMonitoringService;

    private ServiceOrderEntity findEntityById(String id){
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
    }

    public List<ServiceOrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ServiceOrderResponse findById(String id) {
        return mapper.toResponse(this.findEntityById(id));
    }

    public ServiceOrderResponse findByServiceOrderNumber(Long serviceOrderNumber) {
        ServiceOrderEntity entity = repository.findByServiceOrderNumber(serviceOrderNumber)
                .orElseThrow(
                        () -> NotFoundException.of("Service Order ", serviceOrderNumber)
                );
        return mapper.toResponse(entity);
    }

    public ServiceOrderResponse createServiceOrder(NewServiceOrderRequest request) {
        log.info("Criando nova Ordem de Serviço para o cliente ID: {}", request.getCustomerId());
        LaborsDTO labors = laborSelectionService.addLabors(request.getLaborIds(), List.of());
        CustomerDTO customer = customerAndMecnichalService.getCustomer(request.getCustomerId());
        VehicleDTO vehicle = vehicleSelectionService.getVehicle(request.getVehicleId());
        ServiceOrderEntity entity = mapper.toCreateEntity(request, vehicle, customer, labors);
        statusService.updateStatus(entity, ServiceOrderStatus.RECEBIDA);
        ServiceOrderEntity saved = repository.save(entity);
        log.info("Ordem de Serviço criada com sucesso. ID: {}, Número: {}", saved.getId(), saved.getServiceOrderNumber());
        return mapper.toResponse(saved);
    }

    public ServiceOrderResponse updateServiceOrder(String id, ExistServiceOrderRequest request){
        log.info("Atualizando Ordem de Serviço ID: {}", id);
        ServiceOrderEntity entity = this.findEntityById(id);
        MechanicDTO mechanic = (request.getMechanicId() == null || request.getMechanicId().isEmpty()) ? null :customerAndMecnichalService.getMechanic(request.getMechanicId());
        
        ServiceOrderEntity updated = repository.save(mapper.toUpdateEntity(request, entity, mechanic));
        log.info("Ordem de Serviço ID: {} atualizada com sucesso.", id);
        return mapper.toResponse(updated);
    }

    public void deleteServiceOrder(String id) {
        log.warn("Excluindo Ordem de Serviço ID: {}", id);
        repository.deleteById(id);
        log.info("Ordem de Serviço ID: {} excluída.", id);
    }

    public ServiceOrderResponse addLaborsInServiceOrder(String id, List<LaborsRequest> laborsIdList){
        log.info("Adicionando {} serviço(s) à O.S. ID: {}", laborsIdList.size(), id);
        ServiceOrderEntity entity = this.findEntityById(id);
        LaborsDTO labors = laborSelectionService.addLabors(laborsIdList, entity.getLabors().getLaborsDetails());
        entity.setLabors(labors);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse removeLaborFromServiceOrder(String id, String laborId) {
        log.info("Removendo serviço ID: {} da O.S. ID: {}", laborId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        List<LaborDetailDTO> laborsDetails = entity.getLabors().getLaborsDetails();
        laborsDetails.removeIf(labor -> labor.getLaborId().equals(laborId));
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(laborsDetails);
        laborSelectionService.calculateTotalLaborsAmount(labors);
        entity.setLabors(labors);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse addSupplyFromServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        log.info("Adicionando {} suprimento(s) à O.S. ID: {}", supplyIdList.size(), id);
        ServiceOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        SupplyDTO supply = supplySelectionService.addSupplys(
                supplyIdList,
                (entity.getSupplys() == null) ? List.of() : entity.getSupplys().getSupplysDetails()
        );
        entity.setSupplys(supply);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse removeSupplyFromServiceOrder(String id, String supplyId) {
        log.info("Removendo suprimento ID: {} da O.S. ID: {}", supplyId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        supplySelectionService.removeSupply(entity.getSupplys(), supplyId);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse updateStatus(String id, ServiceOrderStatus status){
        log.info("Alterando status da O.S. ID: {} para {}", id, status);
        ServiceOrderEntity entity = this.findEntityById(id);
        statusService.updateStatus(entity, status);
        ServiceOrderEntity saved = repository.save(entity);
        log.info("Status da O.S. ID: {} alterado para {} com sucesso.", id, status);
        return mapper.toResponse(saved);
    }

    public ServiceOrderResponse startLabor(String id, String laborId){
        log.info("Iniciando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        statusService.validateStatusForStartExecution(entity);
        
        boolean found = false;
        for(LaborDetailDTO labor : entity.getLabors().getLaborsDetails()){
            if(labor.getLaborId().equals(laborId)){
                if(labor.getStartDate() == null) {
                    labor.setStartDate(LocalDateTime.now());
                    found = true;
                    break;
                } else {
                    log.error("Tentativa de iniciar serviço já iniciado. O.S. ID: {}, Labor ID: {}", id, laborId);
                    throw new DomainException("O serviço já foi iniciado");
                }
            }
        }
        
        if (!found) {
            log.error("Serviço ID: {} não encontrado na O.S. ID: {}", laborId, id);
            throw new NotFoundException("A O.S não possui este serviço");
        }
        if(entity.getStatus().equals(ServiceOrderStatus.APROVADA)){
            statusService.updateStatus(entity, ServiceOrderStatus.EM_EXECUCAO);
        }
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse finishLabor(String id, String laborId){
        log.info("Finalizando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        statusService.validateStatusForStartExecution(entity);
        
        boolean found = false;
        for(LaborDetailDTO labor : entity.getLabors().getLaborsDetails()){
            if(labor.getLaborId().equals(laborId)){
                if(labor.getEndDate() == null && labor.getStartDate() != null) {
                    found = true;
                    labor.setEndDate(LocalDateTime.now());
                    laborMonitoringService.updateExecutionTimeInDays(
                            laborId,
                            labor.getStartDate(),
                            labor.getEndDate()
                    );
                    break;
                } else {
                    log.error("Falha ao finalizar serviço. Verifique se foi iniciado ou se já está finalizado. O.S. ID: {}, Labor ID: {}", id, laborId);
                    throw new DomainException("Não é possível finalizar um serviço que não foi iniciado ou já foi finalizado.");
                }
            }
        }

        if (!found) {
            log.error("Serviço ID: {} não encontrado na O.S. ID: {}", laborId, id);
            throw new NotFoundException("A O.S não possui este serviço");
        }

        return mapper.toResponse(repository.save(entity));
    }
}
