package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    private final CustomerAndMecnichalService customerService;

    private final ServiceOrderMapper mapper;

    private final ServiceOrderService serviceOrderService;
    private final StatusService statusService;

    public List<ServiceOrderResponse> findByCustomerDocument(String document, ServiceOrderStatus status) {
        log.info("Iniciando consulta de ordens de serviço para o documento: {} com status: {}", document, status != null ? status : "TODOS");
        
        CustomerResponse customerResponse = customerService.getCustomerByDocument(document);
        log.debug("Cliente identificado para o documento {}: ID {}", document, customerResponse.id());

        List<ServiceOrderEntity> entityList = serviceOrderRepository.findByCustomerId(customerResponse.id());
        log.debug("Total de ordens encontradas no banco para o cliente {}: {}", customerResponse.id(), entityList.size());

        List<ServiceOrderResponse> response = new ArrayList<>();
        if(status == null){
            entityList.forEach(
                    item -> response.add(mapper.toResponse(item))
            );
        } else {
            log.debug("Filtrando ordens pelo status: {}", status);
            entityList.stream()
                    .filter(item -> item.getStatus().equals(status))
                    .forEach(item -> response.add(mapper.toResponse(item)));
        }

        log.info("Consulta finalizada. Retornando {} ordens de serviço para o documento: {}", response.size(), document);
        return response;
    }

    public ServiceOrderResponse updateLaborSituation(String serviceOrderId, List<ModifySituationRequest> request) {
        ServiceOrderEntity entity = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> NotFoundException.of("Service Order", serviceOrderId));
        if(entity.getStatus().equals(ServiceOrderStatus.AGUARDANDO_APROVACAO)) {
            LocalDateTime now = LocalDateTime.now();
            Map<String, LaborSituation> laborsToUpdateMap = request.stream()
                    .collect(Collectors.toMap(
                            ModifySituationRequest::laborId,
                            ModifySituationRequest::situation
                    ));
            entity.getLabors().getLaborsDetails().stream()
                    .filter(item -> laborsToUpdateMap.containsKey(item.getLaborId()))
                    .forEach(item -> {
                        LaborSituation newSituation = laborsToUpdateMap.get(item.getLaborId());
                        item.setSituation(newSituation);
                        item.setSituationDate(now);
                    });
            statusService.updateStatus(entity, ServiceOrderStatus.APROVADA);
        } else {
            throw new DomainException("Só é possivel atualizar a situação de um serviço para O.S AGUARDANDO APROVAÇÃO");
        }
        return mapper.toResponse(serviceOrderService.save(entity));
    }
}
