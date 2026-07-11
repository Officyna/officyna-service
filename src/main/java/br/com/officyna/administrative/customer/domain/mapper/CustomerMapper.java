package br.com.officyna.administrative.customer.domain.mapper;


import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .name(request.name())
                .document(request.document())
                .type(request.type())
                .email(request.email())
                .phone(request.phone())
                .areaCode(request.areaCode())
                .countryCode(request.countryCode())
                .address(toAddressEntity(request.address()))
                .build();
    }

    private Address toAddressEntity(AddressDTO address) {
        if (address == null) return null;
        return Address.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode())
                .country(address.country())
                .build();
    }
}