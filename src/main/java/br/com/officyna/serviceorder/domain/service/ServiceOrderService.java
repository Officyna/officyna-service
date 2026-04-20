package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
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
        ServiceOrderEntity entity = mapper.toCreateEntity(request, vehicle, customer, labors, ServiceOrderStatus.RECEBIDA);

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
        this.updateStatusDateByStatus(entity, status);
        ServiceOrderEntity saved = repository.save(entity);
        log.info("Status da O.S. ID: {} alterado para {} com sucesso.", id, status);
        return mapper.toResponse(saved);
    }

    public ServiceOrderResponse startLabor(String id, String laborId){
        log.info("Iniciando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        this.validateExecutionStatus(entity);
        
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

        this.updateStatusDateByStatus(entity, ServiceOrderStatus.EM_EXECUCAO);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse finishLabor(String id, String laborId){
        log.info("Finalizando execução do serviço ID: {} na O.S. ID: {}", laborId, id);
        ServiceOrderEntity entity = this.findEntityById(id);
        this.validateExecutionStatus(entity);
        
        boolean found = false;
        for(LaborDetailDTO labor : entity.getLabors().getLaborsDetails()){
            if(labor.getLaborId().equals(laborId)){
                if(labor.getEndDate() == null && labor.getStartDate() != null) {
                    labor.setEndDate(LocalDateTime.now());
                    found = true;
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

    private void validateExecutionStatus(ServiceOrderEntity entity) {
        if (!(ServiceOrderStatus.APROVADA.equals(entity.getStatus()) || ServiceOrderStatus.EM_EXECUCAO.equals(entity.getStatus()))) {
            log.warn("Tentativa de operar serviços em O.S. com status inválido: {}. ID: {}", entity.getStatus(), entity.getId());
            throw new DomainException("Um serviço só pode ser iniciado ou finalizado se o status da ordem de serviço for APROVADA ou EM EXECUÇÃO.");
        }
        LaborsDTO labors = entity.getLabors();
        if (labors == null || labors.getLaborsDetails() == null) {
            throw new DomainException("A ordem de serviço não possui serviços cadastrados.");
        }
    }

    public void updateStatusDateByStatus(ServiceOrderEntity entity, ServiceOrderStatus status){
        log.debug("Validando transição de status para a O.S. ID: {}. De {} para {}", entity.getId(), entity.getStatus(), status);
        if(status.equals(entity.getStatus())) {
            throw new DomainException("A Ordem de Serviço já foi processada com status " + status.getStatusName() + ".");
        }
        if(status.equals(ServiceOrderStatus.RECEBIDA)){
            throw new DomainException("A Ordem de Serviço já foi recebida e não pode retornar a este status.");
        }else if(status.equals(ServiceOrderStatus.EM_DIAGNOSTICO)){
            if (!ServiceOrderStatus.RECEBIDA.equals(entity.getStatus())) {
                throw new DomainException("Para iniciar o diagnóstico, a O.S. deve estar no status RECEBIDA.");
            }
        }else if(status.equals(ServiceOrderStatus.AGUARDANDO_APROVACAO)){
            if (!ServiceOrderStatus.EM_DIAGNOSTICO.equals(entity.getStatus())) {
                throw new DomainException("Para aguardar aprovação, a O.S. deve ter passado pelo diagnóstico.");
            }
        }else if(status.equals(ServiceOrderStatus.APROVADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser aprovadas.");
            }
        }else if(status.equals(ServiceOrderStatus.EM_EXECUCAO)){
            if (!ServiceOrderStatus.APROVADA.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens APROVADAS podem entrar em execução.");
            }
        }else if(status.equals(ServiceOrderStatus.FINALIZADA)){
            if (!ServiceOrderStatus.EM_EXECUCAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens EM EXECUÇÃO podem ser finalizadas.");
            }
        }else if(status.equals(ServiceOrderStatus.ENTREGUE)){
            if (!ServiceOrderStatus.FINALIZADA.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordes FINALIZADAS podem ser consideradas entregues");
            }
        }else if(status.equals(ServiceOrderStatus.RECUSADA)){
            if (!ServiceOrderStatus.AGUARDANDO_APROVACAO.equals(entity.getStatus())) {
                throw new DomainException("Apenas ordens AGUARDANDO APROVAÇÃO podem ser recusadas.");
            }
        }
        entity.setStatusDate(status);
    }
}
