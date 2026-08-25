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

    public CustomerServiceOrderService(
            ServiceOrderRepository repository,
            CustomerAndMecnichalService customerService,
            ServiceOrderService serviceOrderService) {
        this.repository = repository;
        this.customerService = customerService;
        this.serviceOrderService = serviceOrderService;
    }

    public List<ServiceOrder> findByCustomerDocument(
            String document,
            ServiceOrderStatus status) {

        log.info(
                "Starting service order search for customer document: {} with status: {}",
                document,
                status != null ? status : "ALL"
        );

        Customer customer = customerService.getCustomerByDocument(document);

        log.debug(
                "Customer identified for document {}: id={}",
                document,
                customer.getId()
        );

        List<ServiceOrder> entityList =
                repository.findByCustomerId(customer.getId());

        log.debug(
                "Total service orders found for customer {}: {}",
                customer.getId(),
                entityList.size()
        );

        List<ServiceOrder> result = (status == null)
                ? entityList
                : entityList.stream()
                  .filter(item -> item.getStatus().equals(status))
                  .toList();

        log.info(
                "Service order search completed. Returning {} orders for customer document: {}",
                result.size(),
                document
        );

        return result;
    }

    public ServiceOrder updateLaborSituation(
            String serviceOrderId,
            List<ModifySituationRequest> request) {

        log.info(
                "Updating labor situation for service order: {}",
                serviceOrderId
        );

        ServiceOrder entity = repository.findById(serviceOrderId)
                .orElseThrow(() -> {
                    log.warn(
                            "Service order not found for labor situation update: {}",
                            serviceOrderId
                    );
                    return ServiceOrderNotFoundException.of(serviceOrderId);
                });

        log.debug(
                "Service order {} found with current status: {}",
                serviceOrderId,
                entity.getStatus()
        );

        if (entity.getStatus().equals(ServiceOrderStatus.AGUARDANDO_APROVACAO)) {

            LocalDateTime now = LocalDateTime.now();

            Map<String, LaborSituation> laborsToUpdateMap =
                    request.stream()
                            .collect(Collectors.toMap(
                                    ModifySituationRequest::laborId,
                                    ModifySituationRequest::situation
                            ));

            log.debug(
                    "Updating {} labor situations for service order: {}",
                    laborsToUpdateMap.size(),
                    serviceOrderId
            );

            entity.getLabors().getLaborsDetails().stream()
                    .filter(item -> laborsToUpdateMap.containsKey(item.getLaborId()))
                    .forEach(item -> {
                        LaborSituation newSituation =
                                laborsToUpdateMap.get(item.getLaborId());

                        log.debug(
                                "Updating labor {} situation from {} to {}",
                                item.getLaborId(),
                                item.getSituation(),
                                newSituation
                        );

                        item.setSituation(newSituation);
                        item.setSituationDate(now);
                    });

            ServiceOrderStatus previousStatus = entity.getStatus();

            entity.setStatus(ServiceOrderStatus.APROVADA);

            serviceOrderService.logStatusChange(
                    entity,
                    previousStatus,
                    ServiceOrderStatus.APROVADA
            );

            log.info(
                    "Service order {} approved after labor situation update",
                    serviceOrderId
            );

        } else {

            log.warn(
                    "Cannot update labor situation for service order {}. Current status: {}",
                    serviceOrderId,
                    entity.getStatus()
            );

            throw new ServiceOrderBusinessException(
                    "Só é possivel atualizar a situação de um serviço para O.S AGUARDANDO APROVAÇÃO"
            );
        }

        ServiceOrder saved = serviceOrderService.save(entity);

        log.info(
                "Labor situation update completed for service order: {}",
                serviceOrderId
        );

        return saved;
    }
}