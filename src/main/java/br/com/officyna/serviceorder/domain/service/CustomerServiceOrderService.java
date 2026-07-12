package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.serviceorder.domain.exception.ServiceOrderBusinessException;
import br.com.officyna.serviceorder.domain.exception.ServiceOrderNotFoundException;
import br.com.officyna.serviceorder.api.resources.ModifySituationRequest;
import br.com.officyna.serviceorder.domain.entity.ServiceOrder;
import br.com.officyna.serviceorder.domain.enums.LaborSituation;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.repository.ServiceOrderRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CustomerServiceOrderService {

    private final ServiceOrderRepository repository;

    private final CustomerAndMecnichalService customerService;

    private final ServiceOrderService serviceOrderService;

    public CustomerServiceOrderService(ServiceOrderRepository repository,
                                       CustomerAndMecnichalService customerService,
                                       ServiceOrderService serviceOrderService) {
        this.repository = repository;
        this.customerService = customerService;
        this.serviceOrderService = serviceOrderService;
    }

    public List<ServiceOrder> findByCustomerDocument(String document, ServiceOrderStatus status) {
        log.info("Iniciando consulta de ordens de serviço para o documento: {} com status: {}", document, status != null ? status : "TODOS");

        Customer customer = customerService.getCustomerByDocument(document);
        log.debug("Cliente identificado para o documento {}: ID {}", document, customer.getId());

        List<ServiceOrder> entityList = repository.findByCustomerId(customer.getId());
        log.debug("Total de ordens encontradas no banco para o cliente {}: {}", customer.getId(), entityList.size());

        List<ServiceOrder> result = (status == null)
                ? entityList
                : entityList.stream().filter(item -> item.getStatus().equals(status)).toList();

        log.info("Consulta finalizada. Retornando {} ordens de serviço para o documento: {}", result.size(), document);
        return result;
    }

    public ServiceOrder updateLaborSituation(String serviceOrderId, List<ModifySituationRequest> request) {
        ServiceOrder entity = repository.findById(serviceOrderId)
                .orElseThrow(() -> ServiceOrderNotFoundException.of(serviceOrderId));
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
            entity.setStatus(ServiceOrderStatus.APROVADA);
        } else {
            throw new ServiceOrderBusinessException("Só é possivel atualizar a situação de um serviço para O.S AGUARDANDO APROVAÇÃO");
        }
        return serviceOrderService.save(entity);
    }
}