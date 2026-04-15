package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.labor.api.resources.LaborResponse;
import br.com.officyna.administrative.labor.domain.service.LaborService;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.administrative.vehicle.api.resources.VehicleResponse;
import br.com.officyna.administrative.vehicle.domain.service.VehicleService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrderService {

    private final ServiceOrderRepository repository;

    private final LaborService laborService;

    private final CustomerService customerService;

    private final VehicleService vehicleService;

    private final UserService userService;

    private final ServiceOrderMapper mapper;

    public List<ServiceOrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public ServiceOrderResponse findById(String id) {
        ServiceOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        return mapper.toResponse(entity);
    }

    public ServiceOrderResponse createServiceOrder(NewServiceOrderRequest request) {
        LaborsDTO labors = this.addLabors(request.getLaborIds(), List.of());
        CustomerDTO customer = this.getCustomer(request.getCustomerId());
        VehicleDTO vehicle = this.getVehicle(request.getVehicleId());
        ServiceOrderEntity entity = mapper.toCreateEntity(request, vehicle, customer, labors, ServiceOrderStatus.RECEBIDA);

        return mapper.toResponse(repository.save(entity));
    }

    public ServiceOrderResponse updateServiceOrder(String id, ExistServiceOrderRequest request){
        ServiceOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        MechanicDTO mechanic = (request.getMechanicId() == null || request.getMechanicId().isEmpty()) ? null :this.getMechanic(request.getMechanicId());
        return mapper.toResponse(repository.save(mapper.toUpdateEntity(request, entity, mechanic)));
    }

    public void deleteServiceOrder(String id) {
        repository.deleteById(id);
    }

    public ServiceOrderResponse addLaborsInServiceOrder(String id, List<IdListRequest> laborsIdList){
        ServiceOrderEntity entity = repository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Service Order", id));
        LaborsDTO labors = this.addLabors(laborsIdList, entity.getLabors().getLaborsDetails());
        entity.setLabors(labors);
        return mapper.toResponse(repository.save(entity));
    }

    private VehicleDTO getVehicle(@NotBlank(message = "ID do Veículo é obrigatório") String id) {
        VehicleResponse response = vehicleService.findById(id);
        return new VehicleDTO(response.id(), response.plate(), response.brand(), response.model(), response.color());
    }

    private CustomerDTO getCustomer(@NotBlank(message = "ID do Cliente é obrigatório") String id) {
        CustomerResponse response = customerService.findById(id);
        return new CustomerDTO(response.id(), 
                response.name(), 
                response.phone(),
                response.address().street(),
                response.address().number(),
                response.address().neighborhood(),
                response.address().city(),
                response.address().state(),
                response.address().zipCode(),
                response.address().complement());
    }

    private MechanicDTO getMechanic(@NotBlank(message = "ID do Mecânico é obrigatório") String id) {
        UserResponse response = userService.findById(id);
        return new MechanicDTO(response.getId(), response.getName());
    }

    private LaborsDTO addLabors(List<IdListRequest> laborsIdList, List<LaborDetailDTO> laborsDetails) {
        List<LaborDetailDTO> allLabors = new ArrayList<>(laborsDetails != null ? laborsDetails : List.of());

        if (laborsIdList != null && !laborsIdList.isEmpty()) {
            List<LaborDetailDTO> newLabors = laborsIdList.stream()
                    .map(id -> {
                        LaborResponse response = laborService.findById(id.getId());
                        return new LaborDetailDTO(response.id(), response.name(), response.price(), null, null);
                    })
                    .toList();
            allLabors.addAll(newLabors);
        }

        BigDecimal totalAmount = allLabors.stream()
                .map(LaborDetailDTO::getLaborPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LaborsDTO labors = new LaborsDTO();
        labors.setLaborsDetails(allLabors);
        labors.setTotalLaborsAmount(totalAmount);
        return labors;
    }

    private SupplyDTO addSupplys(List<String> supplyList){
        /*TODO: Implementar construcao da lista de suprimentos dentro da O.S
         *A O.S recebe a lista de IDs e busca os suprimentos e vincula os preços ou
         *recebe os suprimentos e os preços no request?*/

        List<SupplyDetailDTO> supplysDetails = List.of();
        SupplyDTO supply = new SupplyDTO();

        // Removido setTotalPrice pois o calculo é dinâmico agora no DTO
        supply.setSupplysDetails(supplysDetails);
        return supply;
    }
}
