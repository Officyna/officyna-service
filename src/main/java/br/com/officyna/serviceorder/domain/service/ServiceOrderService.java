package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.supply.domain.service.StockService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.monitoring.domain.service.LaborMonitoringService;
import br.com.officyna.serviceorder.api.resources.*;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
public class ServiceOrderService {

    private final ServiceOrderRepository repository;

    private final LaborSelectionService laborSelectionService;

    private final SupplySelectionService supplySelectionService;

    private final CustomerAndMecnichalService customerAndMecnichalService;

    private final VehicleSelectionService vehicleSelectionService;

    private final ServiceOrderMapper mapper;

    private final LaborMonitoringService laborMonitoringService;

    private final StockService stockService;

    public ServiceOrderService(ServiceOrderRepository repository,
                               LaborSelectionService laborSelectionService,
                               SupplySelectionService supplySelectionService,
                               CustomerAndMecnichalService customerAndMecnichalService,
                               VehicleSelectionService vehicleSelectionService,
                               ServiceOrderMapper mapper,
                               LaborMonitoringService laborMonitoringService,
                               StockService stockService) {
        this.repository = repository;
        this.laborSelectionService = laborSelectionService;
        this.supplySelectionService = supplySelectionService;
        this.customerAndMecnichalService = customerAndMecnichalService;
        this.vehicleSelectionService = vehicleSelectionService;
        this.mapper = mapper;
        this.laborMonitoringService = laborMonitoringService;
        this.stockService = stockService;
    }

    private ServiceOrder findEntityById(String id){
        return repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
    }

    public List<ServiceOrder> findAll() {
        List<ServiceOrder> ordersServiceEntity =  repository.findAll();
        return sortOrdersServiceByStatusAndDate(ordersServiceEntity);
    }

    private List<ServiceOrder> sortOrdersServiceByStatusAndDate(
            List<ServiceOrder> ordersServiceEntity) {

        return ordersServiceEntity.stream()
                .filter(order ->
                        order.getStatus() == ServiceOrderStatus.EM_EXECUCAO
                                || order.getStatus() == ServiceOrderStatus.AGUARDANDO_APROVACAO
                                || order.getStatus() == ServiceOrderStatus.APROVADA
                                || order.getStatus() == ServiceOrderStatus.EM_DIAGNOSTICO
                                || order.getStatus() == ServiceOrderStatus.RECEBIDA)
                .sorted(
                        Comparator
                                .comparingInt((ServiceOrder order) ->
                                        order.getStatus().getPriority())
                                .thenComparing(ServiceOrder::getCreatedAt)
                )
                .toList();
    }

    public ServiceOrder findById(String id) {
        return this.findEntityById(id);
    }

    public ServiceOrder findByServiceOrderNumber(Long serviceOrderNumber) {
        return repository.findByServiceOrderNumber(serviceOrderNumber)
                .orElseThrow(
                        () -> NotFoundException.of("Service Order ", serviceOrderNumber)
                );
    }

    public ServiceOrder createServiceOrder(NewServiceOrderRequest request) {
        log.info("Criando nova Ordem de Serviço para o cliente ID: {}", request.getCustomerId());
        LaborsDTO labors = laborSelectionService.addLabors(request.getLaborIds(), List.of());
        CustomerDTO customer = customerAndMecnichalService.getCustomer(request.getCustomerId());
        VehicleDTO vehicle = vehicleSelectionService.getVehicle(request.getVehicleId());
        ServiceOrder entity = mapper.toCreateEntity(request, vehicle, customer, labors);
        entity.setStatus(ServiceOrderStatus.RECEBIDA);
        ServiceOrder saved = this.save(entity);
        log.info("Ordem de Serviço criada com sucesso. ID: {}, Número: {}", saved.getId(), saved.getServiceOrderNumber());
        return saved;
    }

    public ServiceOrder updateServiceOrder(String id, ExistServiceOrderRequest request){
        log.info("Atualizando Ordem de Serviço ID: {}", id);
        ServiceOrder entity = this.findEntityById(id);
        MechanicDTO mechanic = (request.getMechanicId() == null || request.getMechanicId().isEmpty()) ? null :customerAndMecnichalService.getMechanic(request.getMechanicId());

        ServiceOrder updated = this.save(mapper.toUpdateEntity(request, entity, mechanic));
        log.info("Ordem de Serviço ID: {} atualizada com sucesso.", id);
        return updated;
    }

    public void deleteServiceOrder(String id) {
        log.warn("Excluindo Ordem de Serviço ID: {}", id);
        repository.deleteById(id);
        log.info("Ordem de Serviço ID: {} excluída.", id);
    }

    public ServiceOrder addLaborsInServiceOrder(String id, List<LaborsRequest> laborsIdList){
        log.info("Adicionando {} serviço(s) à O.S. ID: {}", laborsIdList.size(), id);
        ServiceOrder entity = this.findEntityById(id);
        LaborsDTO labors = laborSelectionService.addLabors(laborsIdList, entity.getLabors().getLaborsDetails());
        entity.setLabors(labors);
        return this.save(entity);
    }

    public ServiceOrder removeLaborFromServiceOrder(String id, String laborId) {
        log.info("Removendo serviço ID: {} da O.S. ID: {}", laborId, id);
        ServiceOrder entity = this.findEntityById(id);
        List<LaborDetailDTO> laborsDetails = entity.getLabors().getLaborsDetails();
        laborsDetails.removeIf(labor -> labor.getLaborId().equals(laborId));
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(laborsDetails);
        entity.setLabors(labors);
        return this.save(entity);
    }

    public ServiceOrder addSupplyFromServiceOrder(String id, List<SupplysRequest> supplyIdList) {
        log.info("Adicionando {} suprimento(s) à O.S. ID: {}", supplyIdList.size(), id);
        ServiceOrder entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        SupplyDTO supply = supplySelectionService.addSupplys(
                supplyIdList,
                (entity.getSupplys() == null) ? List.of() : entity.getSupplys().getSupplysDetails()
        );
        entity.setSupplys(supply);
        return this.save(entity);
    }

    public ServiceOrder removeSupplyFromServiceOrder(String id, String supplyId) {
        log.info("Removendo suprimento ID: {} da O.S. ID: {}", supplyId, id);
        ServiceOrder entity = this.findEntityById(id);
        supplySelectionService.removeSupply(entity.getSupplys(), supplyId);
        return this.save(entity);
    }

    public ServiceOrder updateStatus(String id, ServiceOrderStatus status){
        log.info("Alterando status da O.S. ID: {} para {}", id, status);
        ServiceOrder entity = this.findEntityById(id);
        entity.setStatus(status);
        if(status.equals(ServiceOrderStatus.FINALIZADA) && stockService != null) stockService.releaseSupplies(entity.getSupplys().getSupplysDetails());
        ServiceOrder saved = this.save(entity);
        log.info("Status da O.S. ID: {} alterado para {} com sucesso.", id, status);
        return saved;
    }

    public ServiceOrder startLabor(String id, String laborId){
        log.info("Iniciando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrder entity = this.findEntityById(id);
        this.validateStatusForStartExecution(entity);

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
            entity.setStatus(ServiceOrderStatus.EM_EXECUCAO);
            if (stockService != null) stockService.consumeSupplies(entity.getSupplys().getSupplysDetails());
        }
        return this.save(entity);
    }

    public ServiceOrder finishLabor(String id, String laborId){
        log.info("Finalizando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrder entity = this.findEntityById(id);
        this.validateStatusForStartExecution(entity);

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

        return this.save(entity);
    }

    public ServiceOrder save(ServiceOrder entity){
        entity.calculateBudget();
        return repository.save(entity);
    }

    public void sendToCustomer(String id) {
        ServiceOrder serviceOrder = this.findEntityById(id);
        serviceOrder.setStatus(ServiceOrderStatus.AGUARDANDO_APROVACAO);
        if (stockService != null) stockService.reserveSupplies(serviceOrder.getSupplys().getSupplysDetails());
        repository.save(serviceOrder);
    }

    private void validateStatusForStartExecution(ServiceOrder entity) {
        if (!(ServiceOrderStatus.APROVADA.equals(entity.getStatus()) || ServiceOrderStatus.EM_EXECUCAO.equals(entity.getStatus()))) {
            log.warn("Falha na validação: Tentativa de operar serviços em O.S. com status inválido. Status atual: {}, O.S. ID: {}", entity.getStatus(), entity.getId());
            throw new DomainException("Um serviço só pode ser iniciado ou finalizado se o status da ordem de serviço for APROVADA ou EM EXECUÇÃO.");
        }

        LaborsDTO labors = entity.getLabors();
        if (labors == null || labors.getLaborsDetails() == null || labors.getLaborsDetails().isEmpty()) {
            log.warn("Falha na validação: Tentativa de iniciar execução em O.S. sem serviços cadastrados. O.S. ID: {}", entity.getId());
            throw new DomainException("A ordem de serviço não possui serviços cadastrados.");
        }
    }
}