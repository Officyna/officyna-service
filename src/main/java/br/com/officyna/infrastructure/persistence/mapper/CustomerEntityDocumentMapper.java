package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.customer.domain.entity.Address;
import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.entity.CustomerType;
import br.com.officyna.infrastructure.persistence.mongodb.model.AddressDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre CustomerEntity (domínio) e CustomerDocument (infraestrutura).
 */
@Component
public class CustomerEntityDocumentMapper {

    public CustomerDocument toDocument(Customer entity) {
        if (entity == null) {
            return null;
        }
        return CustomerDocument.builder()
                .id(entity.getId())
                .name(entity.getName())
                .document(entity.getDocument())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .areaCode(entity.getAreaCode())
                .countryCode(entity.getCountryCode())
                .address(toAddressDocument(entity.getAddress()))
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public Customer toEntity(CustomerDocument document) {
        if (document == null) {
            return null;
        }
        return Customer.builder()
                .id(document.getId())
                .name(document.getName())
                .document(document.getDocument())
                .type(document.getType() != null ? CustomerType.valueOf(document.getType()) : null)
                .email(document.getEmail())
                .phone(document.getPhone())
                .areaCode(document.getAreaCode())
                .countryCode(document.getCountryCode())
                .address(toAddressEntity(document.getAddress()))
                .active(document.getActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    private AddressDocument toAddressDocument(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDocument.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .build();
    }

    private Address toAddressEntity(AddressDocument address) {
        if (address == null) {
            return null;
        }
        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .country(address.getCountry())
                .build();
    }
}

