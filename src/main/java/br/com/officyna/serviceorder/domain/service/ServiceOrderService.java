package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.ExistServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.IdListRequest;
import br.com.officyna.serviceorder.api.resources.NewServiceOrderRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.dto.*;
import br.com.officyna.serviceorder.domain.enitity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository repository;

    private final LaborSelectionService laborSelectionService;

    private final SupplySelectionService supplySelectionService;

    private final CustomerAndMecnichalService customerAndMecnichalService;

    private final VehicleService vehicleService;

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

    public ServiceOrderResponse createServiceOrder(NewServiceOrderRequest request) {
        LaborsDTO labors = laborSelectionService.addLabors(request.getLaborIds(), List.of());
        CustomerDTO customer = customerAndMecnichalService.getCustomer(request.getCustomerId());
        VehicleDTO vehicle = this.getVehicle(request.getVehicleId());
        ServiceOrderEntity entity = mapper.toCreateEntity(request, vehicle, customer, labors, ServiceOrderStatus.RECEBIDA);

        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse updateServiceOrder(String id, ExistServiceOrderRequest request){
        ServiceOrderEntity entity = this.findEntityById(id);
        MechanicDTO mechanic = (request.getMechanicId() == null || request.getMechanicId().isEmpty()) ? null :customerAndMecnichalService.getMechanic(request.getMechanicId());
        return mapper.toResponse(repository.save(mapper.toUpdateEntity(request, entity, mechanic)));
    }

    public void deleteServiceOrder(String id) {
        repository.deleteById(id);
    }

    public ServiceOrderResponse addLaborsInServiceOrder(String id, List<IdListRequest> laborsIdList){
        ServiceOrderEntity entity = this.findEntityById(id);
        LaborsDTO labors = laborSelectionService.addLabors(laborsIdList, entity.getLabors().getLaborsDetails());
        entity.setLabors(labors);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse removeLaborFromServiceOrder(String id, String laborId) {
        ServiceOrderEntity entity = this.findEntityById(id);
        List<LaborDetailDTO> laborsDetails = entity.getLabors().getLaborsDetails();
        laborsDetails.removeIf(labor -> labor.getLaborId().equals(laborId));
        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(laborsDetails);
        labors.calculateTotalLaborsAmount();
        entity.setLabors(labors);
        return mapper.toResponse(repository.save(entity));
    }

    private VehicleDTO getVehicle(@NotBlank(message = "ID do Veículo é obrigatório") String id) {
        VehicleResponse response = vehicleService.findById(id);
        return new VehicleDTO(response.id(), response.plate(), response.brand(), response.model(), response.color());
    }

    public ServiceOrderResponse addSupplyFromServiceOrder(String id, List<IdListRequest> supplyIdList) {
        ServiceOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        SupplyDTO supply = supplySelectionService.addSupplys(supplyIdList, entity.getSupplys().getSupplysDetails());
        entity.setSupplys(supply);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse removeSupplyFromServiceOrder(String id, String supplyId) {
        ServiceOrderEntity entity = this.findEntityById(id);
        List<SupplyDetailDTO> supplysDetails = entity.getSupplys().getSupplysDetails();
        supplysDetails.removeIf(supply -> supply.getId().equals(supplyId));
        SupplyDTO supplys = new SupplyDTO();
        supplys.setSupplysDetails(supplysDetails);
        supplys.calculateTotalSupplyAmount();
        entity.setSupplys(supplys);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse updateStatus(String id, ServiceOrderStatus status){
        ServiceOrderEntity entity = this.findEntityById(id);
        entity.setStatus(status);
        entity.setStatusDate(status);
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse startLabor(String id, String laborId){
        ServiceOrderEntity entity = this.findEntityById(id);
        this.validateExecutionStatus(entity);
        for(LaborDetailDTO labor : entity.getLabors().getLaborsDetails()){
            if(labor.getLaborId().equals(laborId)){
                if(labor.getStartDate() == null) {
                    labor.setStartDate(LocalDateTime.now());
                    break;
                } else {
                    throw new DomainException("O serviço já foi iniciado");
                }
            } else{
                throw new NotFoundException("A O.S não possui este serviço");
            }
        }
        if(entity.getExecutionStartDate() == null) entity.setExecutionStartDate(LocalDateTime.now());
        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse finishLabor(String id, String laborId){
        ServiceOrderEntity entity = this.findEntityById(id);
        this.validateExecutionStatus(entity);
        for(LaborDetailDTO labor : entity.getLabors().getLaborsDetails()){
            if(labor.getLaborId().equals(laborId)){
                if(labor.getEndDate() == null && labor.getStartDate() != null) {
                    labor.setEndDate(LocalDateTime.now());
                    break;
                } else {
                    throw new DomainException("Não é possível finalizar um serviço que não foi iniciado.");
                }
            } else{
                throw new NotFoundException("A O.S não possui este serviço");
            }
        }
        return mapper.toResponse(repository.save(entity));
    }

    private void validateExecutionStatus(ServiceOrderEntity entity) {
        if (!ServiceOrderStatus.APROVADA.equals(entity.getStatus())) {
            throw new DomainException("Um serviço só pode ser iniciado ou finalizado se o status da ordem de serviço for APROVADA.");
        }
        LaborsDTO labors = entity.getLabors();
        if (labors == null || labors.getLaborsDetails() == null) {
            throw new DomainException("A ordem de serviço não possui serviços cadastrados.");
        }
    }
}
