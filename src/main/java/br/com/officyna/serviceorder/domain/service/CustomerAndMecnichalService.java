package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;

public class CustomerAndMecnichalService {

    private final UserService userService;

    private final CustomerService customerService;

    public CustomerAndMecnichalService(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    CustomerDTO getCustomer(String id) {
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

    MechanicDTO getMechanic(String id) {
        UserResponse response = userService.findById(id);
        return new MechanicDTO(response.getId(), response.getName());
    }
}
