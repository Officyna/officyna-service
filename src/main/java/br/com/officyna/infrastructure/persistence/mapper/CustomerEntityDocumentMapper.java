package br.com.officyna.infrastructure.persistence.mapper;

import br.com.officyna.administrative.customer.domain.AddressEntity;
import br.com.officyna.administrative.customer.domain.CustomerEntity;
import br.com.officyna.administrative.customer.domain.CustomerType;
import br.com.officyna.infrastructure.persistence.mongodb.model.AddressDocument;
import br.com.officyna.infrastructure.persistence.mongodb.model.CustomerDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter entre CustomerEntity (domínio) e CustomerDocument (infraestrutura).
 */
@Component
public class CustomerEntityDocumentMapper {

    public CustomerDocument toDocument(CustomerEntity entity) {
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

    public CustomerEntity toEntity(CustomerDocument document) {
        if (document == null) {
            return null;
        }
        return CustomerEntity.builder()
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

    private AddressDocument toAddressDocument(AddressEntity address) {
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

    private AddressEntity toAddressEntity(AddressDocument address) {
        if (address == null) {
            return null;
        }
        return AddressEntity.builder()
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

