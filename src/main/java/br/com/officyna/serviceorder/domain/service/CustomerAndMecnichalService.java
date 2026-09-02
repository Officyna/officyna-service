package br.com.officyna.serviceorder.domain.service;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.service.CustomerService;
import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.serviceorder.domain.dto.CustomerDTO;
import br.com.officyna.serviceorder.domain.dto.MechanicDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerAndMecnichalService {

    private final UserService userService;
    private final CustomerService customerService;

    public CustomerAndMecnichalService(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    CustomerDTO getCustomer(String id) {
        log.info("Finding customer by id: {}", id);

        Customer customer = customerService.findById(id);

        log.debug("Customer found by id: {}", id);

        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getAddress().getStreet(),
                customer.getAddress().getNumber(),
                customer.getAddress().getNeighborhood(),
                customer.getAddress().getCity(),
                customer.getAddress().getState(),
                customer.getAddress().getZipCode(),
                customer.getAddress().getComplement()
        );
    }

    Customer getCustomerByDocument(String document) {
        log.info("Finding customer by document: {}", document);

        Customer customer = customerService.findByDocument(document);

        log.debug("Customer found by document: {}", document);

        return customer;
    }

    MechanicDTO getMechanic(String id) {
        log.info("Finding mechanic by id: {}", id);

        User user = userService.findById(id);

        log.debug("Mechanic found by id: {}", id);

        return new MechanicDTO(user.getId(), user.getName());
    }
}