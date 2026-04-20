package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.serviceorder.api.resources.ServiceOrderResponse;
import br.com.officyna.serviceorder.domain.entity.ServiceOrderEntity;
import br.com.officyna.serviceorder.domain.enums.ServiceOrderStatus;
import br.com.officyna.serviceorder.domain.mapper.ServiceOrderMapper;
import br.com.officyna.serviceorder.repository.ServiceOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceOrderService {

    private final ServiceOrderRepository serviceOrderRepository;

    private final CustomerAndMecnichalService customerService;

    private final ServiceOrderMapper mapper;

    public List<ServiceOrderResponse> findByCustomerDocument(String document, ServiceOrderStatus status) {
        CustomerResponse customerResponse = customerService.getCustomerByDocument(document);
        List<ServiceOrderEntity> entityList = serviceOrderRepository.findByCustomerId(customerResponse.id());
        List<ServiceOrderResponse> response = new ArrayList<>();
        if(status == null){
            entityList.forEach(
                    item -> response.add(mapper.toResponse(item))
            );
        } else {
            entityList.stream()
                    .filter(item -> item.getStatus().equals(status))
                    .forEach(item -> response.add(mapper.toResponse(item)));
        }
        return response;
    }
}
