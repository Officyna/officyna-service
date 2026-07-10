package br.com.officyna.administrative.customer.domain.presenter;

import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerPresenter {

    public CustomerResponse toResponse(Customer entity) {
        return new CustomerResponse(
                entity.getId(),
                entity.getName(),
                entity.getDocument(),
                entity.getType(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAreaCode(),
                entity.getCountryCode(),
                toAddressRecord(entity.getAddress()),
                entity.getActive(),
                entity.getCreatedAt()
        );
    }

    private AddressDTO toAddressRecord(Address entity) {
        if (entity == null) return null;
        return new AddressDTO(
                entity.getStreet(),
                entity.getNumber(),
                entity.getComplement(),
                entity.getNeighborhood(),
                entity.getCity(),
                entity.getState(),
                entity.getZipCode(),
                entity.getCountry()
        );
    }
}