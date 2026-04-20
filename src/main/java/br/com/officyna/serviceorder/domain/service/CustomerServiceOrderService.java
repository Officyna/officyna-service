package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    private final CustomerAndMecnichalService customerService;

    private final ServiceOrderMapper mapper;

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
}
