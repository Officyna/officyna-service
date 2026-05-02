package br.com.officyna.administrative.customer.domain.mapper;


import br.com.officyna.administrative.customer.api.resources.AddressDTO;
import br.com.officyna.administrative.customer.api.resources.CustomerRequest;
import br.com.officyna.administrative.customer.api.resources.CustomerResponse;
import br.com.officyna.administrative.customer.domain.AddressEntity;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.validation.DocumentUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerEntity toEntity(CustomerRequest request) {
        return CustomerEntity.builder()
                .name(request.name())
                .document(DocumentUtils.normalize(request.document()))
                .type(request.type())
                .email(request.email())
                .phone(request.phone())
                .areaCode(request.areaCode())
                .countryCode(request.countryCode())
                .address(toAddressEntity(request.address()))
                .active(true)
                .build();
    }

    public CustomerResponse toResponse(CustomerEntity entity) {
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

    public void updateEntity(CustomerEntity entity, CustomerRequest request) {
        entity.setName(request.name());
        entity.setDocument(DocumentUtils.normalize(request.document()));
        entity.setType(request.type());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setAreaCode(request.areaCode());
        entity.setCountryCode(request.countryCode());
        entity.setAddress(toAddressEntity(request.address()));
    }

    // --- helpers privados ---

    private AddressEntity toAddressEntity(AddressDTO address) {
        if (address == null) return null;
        return AddressEntity.builder()
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

    private AddressDTO toAddressRecord(AddressEntity entity) {
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