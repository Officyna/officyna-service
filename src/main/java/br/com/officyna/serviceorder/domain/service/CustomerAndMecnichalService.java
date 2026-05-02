package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomerAndMecnichalService {

    private final UserService userService;

    private final CustomerService customerService;

    CustomerDTO getCustomer(@NotBlank(message = "ID do Cliente é obrigatório") String id) {
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

    CustomerResponse getCustomerByDocument(String document) {
        return customerService.findByDocument(document);
    }

    MechanicDTO getMechanic(@NotBlank(message = "ID do Mecânico é obrigatório") String id) {
        UserResponse response = userService.findById(id);
        return new MechanicDTO(response.getId(), response.getName());
    }
}
