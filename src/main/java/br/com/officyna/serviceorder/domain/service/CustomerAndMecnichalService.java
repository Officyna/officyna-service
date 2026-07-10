package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
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
        Customer customer = customerService.findById(id);
        return new CustomerDTO(customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress().getStreet(),
                customer.getAddress().getNumber(),
                customer.getAddress().getNeighborhood(),
                customer.getAddress().getCity(),
                customer.getAddress().getState(),
                customer.getAddress().getZipCode(),
                customer.getAddress().getComplement());
    }

    Customer getCustomerByDocument(String document) {
        return customerService.findByDocument(document);
    }

    MechanicDTO getMechanic(String id) {
        UserResponse response = userService.findById(id);
        return new MechanicDTO(response.getId(), response.getName());
    }
}
